# 功能

- 通过注解实现分布式锁
- 通过模板类实现分布式锁

# 注解分布式锁

在需要实现分布式锁的方法上写上如下注解即可实现分布式锁

```java
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DistributedLock {

    /**
     * 锁的key值, 可使用SpEL传方法参数
     * @return key
     */
    String lockKey() default "defaultLock";

    /**
     * 尝试获取锁等待时间, 单位为s
     */
    long waitTime() default 3;

    /**
     * 尝试获取锁的次数,默认为5次
     */
    int tryLockCount() default 5;
}
```

# 模板类实现分布式锁

接口:

```java
public abstract class DistributedLockService {
    /**
     * 执行分布式锁
     * @param lockKey 锁的key
     * @param tryLockCount 尝试获取锁的次数
     * @param waitTime 尝试获取锁的超时时间, 单位是s
     */
    public abstract <T> DistributedLockResult<T> exec(String lockKey, long tryLockCount, long waitTime, Supplier<T> supplier);
}
```



使用方式:

```java
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
```

