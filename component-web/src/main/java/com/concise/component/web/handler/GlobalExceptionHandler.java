package com.concise.component.web.handler;

import com.concise.component.core.entity.response.Response;
import com.concise.component.core.exception.BaseException;
import com.concise.component.core.exception.BizException;
import com.concise.component.core.exception.PreAuthorizeException;
import com.concise.component.core.utils.LogUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Path;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * 全局异常处理,throw new BusinessException(ResultEnum.FAILURE);要在控制层中使用，如果在服务层使用，不会
 * 走BusinessException方法处理，只会走Exception方法进行处理
 * 以下来自stackoverflow
 * @author shenguangyang
 */
@ControllerAdvice
@RestController
public class GlobalExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    @PostConstruct
    public void init() {
        log.info("全局异常初始化完成");
    }
    /**
     * 请求资源不存在异常
     * @param req
     * @param e
     * @return
     */
    @ExceptionHandler(value = {NoHandlerFoundException.class})
    public Response noHandlerFoundException(HttpServletRequest req, Exception e) {
        return Response.buildFailure("请求资源不存在");
    }
    /**
     * 当调用接口时候如果没有传入某个参数就会报出当前异常
     * @param req
     * @param e
     * @return
     */
    @ExceptionHandler(value = {MissingServletRequestParameterException.class})
    public Response missingServletRequestParameterException(HttpServletRequest req, Exception e) {
        return Response.buildFailure("参数缺失");
    }

    /**
     * 方法参数类型不匹配
     * @param req
     * @param e
     * @return
     */
    @ExceptionHandler(value = {MethodArgumentTypeMismatchException.class})
    public Response methodArgumentTypeMismatchException(HttpServletRequest req, Exception e) {
        return Response.buildFailure("方法参数类型不匹配");
    }


    /**
     * 参数解析失败
     * @param request
     * @param e
     * @return
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public Response httpMessageNotReadableException(HttpServletRequest request, Exception e) {
        log.error("请求体必须是json格式：{}发生异常：{}", request.getRequestURI(), e.getMessage());
        return Response.buildFailure("请求体必须是json格式");
    }

    /**
     * 处理实体字段校验不通过异常
     * ConstraintViolationException: 普通参数(非 java bean)校验出错时抛出 把校验注解写在参数上
     * MethodArgumentNotValidException：json请求体绑定到java bean上失败时抛出(参数验证失败)
     * BindException：表单提交请求参数绑定到java bean上失败时抛出 这种异常不能在参数对象上加@RequestBody (参数绑定失败)
     */
    @ExceptionHandler({BindException.class, ConstraintViolationException.class, MethodArgumentNotValidException.class})
    public Response handleMethodArgumentNotValidException(Exception e, HttpServletRequest request) {
        log.error("请求：{}发生异常：{}", request.getRequestURI(), e);
        // 错误信息
        StringBuilder sb = new StringBuilder();
        // 错误信息map
        Map<String, String> error = new HashMap<>(16);
        String msg = "";
        // 判断异常就是ConstraintViolationException：普通参数(非 java bean)校验出错时抛出 把校验注解写在参数上
        if (!(e instanceof BindException) && !(e instanceof MethodArgumentNotValidException)) {
            // 遍历校验失败的参数
            for (ConstraintViolation<?> cv : ((ConstraintViolationException) e).getConstraintViolations()) {
                String message = cv.getMessage();
                sb.append(message).append(";");

                Iterator<Path.Node> it = cv.getPropertyPath().iterator();
                Path.Node last = null;
                while (it.hasNext()) {
                    last = (Path.Node)it.next();
                }
                error.put(last != null ? last.getName() : "", message);
            }

        } else {
            // json和表单提交异常
            List<ObjectError> allErrors = null;
            if (e instanceof BindException) {
                allErrors = ((BindException) e).getAllErrors();
            } else {
                allErrors = ((MethodArgumentNotValidException) e).getBindingResult().getAllErrors();
            }
            // 拼接错误信息
            for (ObjectError oe : allErrors) {
                // 获取java bean字段上标注的错误信息
                msg = oe.getDefaultMessage();
                sb.append(msg).append(";");
                if (oe instanceof FieldError) {
                    error.put(((FieldError) oe).getField(), msg);
                } else {
                    error.put(oe.getObjectName(), msg);
                }
            }
        }

        return Response.buildFailure("参数校验失败" + ":" + sb.toString());
    }

    /**
     * 405 - Method Not Allowed
     */
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public Response handleHttpRequestMethodNotSupportedException(
            HttpRequestMethodNotSupportedException e) {
        return Response.buildFailure("不支持当前请求方法");
    }

    /**
     * 415 - Unsupported Media Type
     */
    @ResponseStatus(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public Response handleHttpMediaTypeNotSupportedException(Exception e) {
        log.error("不支持当前媒体类型,{}", e.getMessage());
        return Response.buildFailure("不支持当前媒体类型");
    }

    /**
     * 处理空指针的异常
     * @param req
     * @param e
     * @return
     */
    @ExceptionHandler(value =NullPointerException.class)
    public Response exceptionHandler(HttpServletRequest req, Exception e){
        log.error("发生空指针异常！原因是:",e);
        LogUtils.logExceptionStack(e);
        return Response.buildFailure("服务器异常");
    }

    @ExceptionHandler(value =IllegalArgumentException.class)
    public Response illegalArgumentException(HttpServletRequest req, Exception e){
        log.error("{}",e.getMessage());
        return Response.buildFailure("参数校验失败" + " [ " + e.getMessage() + " ]");
    }
    
    /**
     * 业务异常
     */
    @ExceptionHandler(BizException.class)
    public Response bizException(BizException e, WebRequest request) {
        log.error(LogUtils.logExceptionStack(e));
        return Response.buildFailure(e.getMessage());
    }

    /**
     * 基本异常
     */
    @ExceptionHandler(BaseException.class)
    public Response baseException(BaseException e, WebRequest request) {
        log.error(LogUtils.logExceptionStack(e));
        return Response.buildFailure(e.getMessage());
    }

    /**
     * 声明要捕获的异常
     * @param request 请求体
     * @param e 异常
     * @return 返回的结果
     */
    @ExceptionHandler(Exception.class)
    public Response defultExcepitonHandler(Exception e, WebRequest request) {
        log.error(LogUtils.logExceptionStack(e));
        return Response.buildFailure("未知服务器异常,请联系管理员");
    }

    /**
     * 权限异常
     */
    @ExceptionHandler(PreAuthorizeException.class)
    public Response preAuthorizeException(PreAuthorizeException e) {
        return Response.buildFailure(403L,"没有权限，请联系管理员授权");
    }
}
