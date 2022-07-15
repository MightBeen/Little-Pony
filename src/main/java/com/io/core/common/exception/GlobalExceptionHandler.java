package com.io.core.common.exception;

import com.io.core.common.wrapper.ResultWrapper;
import com.io.portainer.common.exception.ApplyConflictedException;
import com.io.portainer.common.exception.PortainerException;
import io.jsonwebtoken.JwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;


@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * #Description 非法参数异常处理
            * @return 400错误
            * @author me
            * #Date 2022/6/19
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = IllegalArgumentException.class)
    public ResultWrapper handler(IllegalArgumentException e) {
        log.error("Assert异常:-----------{}", e.getMessage());
        return ResultWrapper.fail(e.getMessage());
    }

    /**
     * #Description 通用运行异常处理
            * @return 403错误
            * @author me
            * #Date 2022/6/19
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = {AccessDeniedException.class, JwtException.class})
    public ResultWrapper handler(RuntimeException e) {
        log.error("运行时异常:-----------{}", e.getMessage());
        // e.printStackTrace();
        return ResultWrapper.fail(403, e.getMessage());
    }

    /**
     * 内部异常处理
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = {RuntimeException.class})
    public ResultWrapper handler02(RuntimeException e) {
        log.error("运行时异常:-----------{}", e.getMessage());
         e.printStackTrace();
        return ResultWrapper.fail(500, "服务器内部异常");
    }


    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = PortainerException.class)
    public ResultWrapper handler03(RuntimeException e){
        // TODO: 完成portainer异常处理，异常加入队列中 (409重复创建应catch)
        log.error("Portainer异常:-----------{}", e.getMessage());
        e.printStackTrace();
        return ResultWrapper.fail("Portainer异常");
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = ApplyConflictedException.class)
    public ResultWrapper handler04(RuntimeException e){
        return ResultWrapper.fail(e.getMessage());
    }


    /**
     * #Description 实体校验异常处理
            * @return 400
            * @author me
            * #Date 2022/6/19
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ResultWrapper handler(MethodArgumentNotValidException e) {
        BindingResult result = e.getBindingResult();
        ObjectError objectError = result.getAllErrors().stream().findFirst().get();
        log.error("实体校验异常：----------------{}", objectError.getDefaultMessage());
        return ResultWrapper.fail(objectError.getDefaultMessage());
    }
}
