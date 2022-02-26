package com.concise.component.core.domain;

/**
 * 领域验证器
 * 当对领域实体赋值之后, 可以调用 validation 进行验证数据是否安全, 前提是实体类必须实现 {@link DomainValidation}
 * 类
 *
 * 实际业务经验:
 * 在实际开发中领域实体很有可能有非常多的字段, 如果使用构造器校验也是可以的, 但是带来了维护问题, 比如如下
 * a. 为了使用mapstruct实体转换工具在转换时自动调用有参构造器并执行你的校验逻辑, 你不得不创建一个全参构造器
 * b. 然后全参构造器的参数名称顺序, 必须和属性顺序一致, 否则在编译的时候mapstruct工具会报错
 * c. 如果你使用全参构造器创建对象并且进行赋值, 很有可能将两个String类型的数据顺序搞反了, 而且这种错误一般只有
 *      在测试时候才可能会被发现
 * d. 如果你的类中只有一个全参构造器, 在测试时候, 你明明只需要其中几个参数, 你不得不选择全参构造器或者再创建一个
 *      构造器 (由于你的全参构造器中会校验参数, 估计你会选择全参构造器)
 *
 * 所以权衡一下, 最终选择再创建一个新领域实体并已经赋值之后, 如果想要校验参数就调用本类的静态类,
 * @author shenguangyang
 * @date 2022-02-26 18:08
 */
public interface DomainValidation {
    static <T extends DomainValidation> T verify(T t) {
        t.verify();
        return t;
    }
    void verify();
}
