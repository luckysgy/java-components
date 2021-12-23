package com.concise.component.lock.aspect;

import com.concise.component.core.entity.response.Response;
import com.concise.component.lock.annotation.DistributedLock;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.integration.redis.util.RedisLockRegistry;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

/**
 * redis分布式锁的切面
 */
@Aspect
@Component
public class RedisLockAspect {
    private static final Logger log = LoggerFactory.getLogger(RedisLockAspect.class);
    @Autowired
    private RedisLockRegistry redisLockRegistry;

    @Around(value = "@annotation(distributedLock)")
    public Object redisLock(ProceedingJoinPoint joinPoint,
                                         DistributedLock distributedLock) {
        Object output = null;
        try {
            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            Method method = signature.getMethod();
            Object[] args = joinPoint.getArgs();
            // 获取被拦截方法参数名列表(使用Spring支持类库)
            LocalVariableTableParameterNameDiscoverer localVariableTable = new LocalVariableTableParameterNameDiscoverer();
            String[] paraNameArr = localVariableTable.getParameterNames(method);
            // 使用SPEL进行key的解析
            ExpressionParser parser = new SpelExpressionParser();
            // SPEL上下文
            StandardEvaluationContext context = new StandardEvaluationContext();
            //把方法参数放入SPEL上下文中
            for(int i = 0; i < Objects.requireNonNull(paraNameArr).length; i++) {
                context.setVariable(paraNameArr[i], args[i]);
            }
            String lockKey = distributedLock.lockKey();

            // 使用变量方式传入业务动态数据
            if(lockKey.matches("^#.*.$")) {
                lockKey = parser.parseExpression(lockKey).getValue(context, String.class);
            }

            // registryKey和lockKey自动冒号连接，最终key为REDIS_LOCK:lockKey，值为uuid
            Lock lock = redisLockRegistry.obtain(lockKey);
            try {
                boolean ifLock = false;
                for(int i =0 ; i < distributedLock.tryLockCount(); i++){
                    ifLock = lock.tryLock(distributedLock.waitTime(), TimeUnit.SECONDS);
                    if (ifLock) {
                        break;
                    }
                }
                log.debug("线程[{}]是否获取到了锁：{}", Thread.currentThread().getName(), ifLock);
                // 可以获取到锁，说明当前没有线程在执行该方法
                if (ifLock) {
                    output = joinPoint.proceed();
                } else {
                    Response apiResult = Response.buildFailure(400L,"服务繁忙, 请稍后再试!!!");
                    log.debug("线程[{}]未获取到锁，目前锁详情信息为：{}", Thread.currentThread().getName(), lock);
                    return apiResult;
                }
            } catch (Exception e) {
                log.error("执行核心奖励扫描时出错:{}", e.getMessage());
            } finally {
                log.info("尝试解锁[{}]", lockKey);
                try {
                    lock.unlock();
                    log.info("[{}]解锁成功", lockKey);
                } catch (Exception e) {
                    log.error("解锁dealAction出错:{}", e.getMessage());
                }
            }
        } catch (Throwable e) {
            log.error("aop redis distributed lock error:{}", e.getLocalizedMessage());
        }
        return output;
    }
}
