package com.concise.demo.lock;

import com.concise.component.lock.service.DistributedLockResult;
import com.concise.component.lock.service.DistributedLockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author shenguangyang
 * @date 2021-12-26 9:10
 */
@Service
public class Demo1Service {
    @Autowired
    private DistributedLockService distributedLockService;
    @Autowired
    private Demo1Mapper demo1Mapper;

    public void test() {
        DistributedLockResult<List<Demo1DO>> result = distributedLockService.exec("test", 2, 2, () -> {
            System.out.println("执行需要控制分布式的业务");
            return demo1Mapper.list();
        });
        if (result.getSuccess() && result.getIsGetLock()) {
            System.out.println("分布式锁业务执行成功且获取到锁");
            for (Demo1DO demo1DO : result.getResult()) {
                System.out.println(demo1DO);
            }
        }
    }
}
