package com.concise.component.core.entity.response;

/**
 * @author shenguangyang
 * @date 2021/8/1 6:42
 */
public enum BaseErrorResponse implements ErrorResponseI {
    SUCCESS(200, "成功"),

    /**
     * 权限异常
     */
    PERMISSION_NO(403, "无权限访问"),

    /**
     * 未知错误
     */
    FAILD(500, "操作失败,请联系管理员"),
    EXCEPTION(500, "系统异常,请联系管理员"),

    /**
     * 文件错误
     */
    FILE_NOT_EXIST(1000, "文件不存在"),
    FILE_NOT_DOWNLOAD(1001, "文件没有下载"),
    FILE_NOT_GENERATE(1002, "文件没有生成"),
    FILE_NOT_STORAGE(1003, "文件没有入库"),
    FILE_ALREADY_DOWNLOAD(1004, "文件已经下载"),

    /**
     * 数据错误
     */
    DATA_ALREADY_PEXISTS(1100, "数据已经存在"),

    /**
     * 数据库错误
     */
    SYSTEM_DB_ERROR(1200, "数据库系统错误"),


    /**
     * 注册登录
     */
    RESIGETR_SUCCESS(1300,"注册成功!"),
    RESIGETER_FAIL(1301,"注册失败!"),
    CODE_FAIL(1302,"验证码不一致!"),

    /**
     * check
     */
    BIND_ERROR (1400,"参数校验异常：%s"),
    ACCESS_LIMIT_REACHED (1401,"非法请求!"),
    REQUEST_ILLEGAL (1402,"访问太频繁!"),
    SESSION_ERROR (1403,"会话不存在或者已经失效!"),
    PASSWORD_EMPTY (1404,"登录密码不能为空!"),
    MOBILE_EMPTY (1405,"手机号不能为空!"),
    MOBILE_ERROR (1406,"手机号格式错误!"),
    MOBILE_NOT_EXIST (1407,"手机号不存在!"),
    PASSWORD_ERROR (1408,"密码错误!"),
    USER_NOT_EXIST(1409,"用户不存在！"),
    VERIFY_CODE_ERROR(1410,"验证码错误！"),
    PARAM_ERROR(1411, "参数错误"),

    /**
     * 用户错误
     */
    USER_LOGIN_USERNAME_OR_PASSWORD_MISSING(1500, "用户/密码必须填写"),
    USER_LOGIN_PASSWORD_OUT_OF_RANGE(1501, "用户密码不在指定范围"),
    USER_LOGIN_USERNAME_OUT_OF_RANGE(1502, "用户名不在指定范围"),


    /**
     * 订单模块
     */
    ORDER_NOT_EXIST(60001,"订单不存在"),

    /**
     * 秒杀模块
     */
    MIAO_SHA_OVER(40001,"商品已经秒杀完毕"),
    REPEATE_MIAOSHA(40002,"不能重复秒杀"),
    MIAOSHA_FAIL(40003,"秒杀失败");

    /**
     * 商品模块
     */
    private Integer code;
    private String message;

    BaseErrorResponse(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public Integer getCode() {
        return this.code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getName() {
        return this.name();
    }

    public String getOutputName() {
        return this.name();
    }

    public String toString() {
        return this.getName();
    }
}
