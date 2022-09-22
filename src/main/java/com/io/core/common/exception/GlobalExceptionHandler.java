package com.io.core.common.exception;

import com.io.core.common.wrapper.ResultWrapper;
import com.io.core.mapper.SysUserMapper;
import com.io.portainer.common.exception.ApplyRejectException;
import com.io.portainer.common.exception.PortainerException;
import com.io.portainer.common.exception.WosSysException;
import com.io.portainer.common.utils.connect.WosSysConnector;
import com.io.portainer.data.dto.wos.WosMessageDto;
import io.jsonwebtoken.JwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;


@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    @Autowired
    WosSysConnector wosSysConnector;

    @Autowired
    SysUserMapper sysUserMapper;

    // TODO: 2022/7/25 添加通过工单系统向管理员发送信息功能，并设法处理给管理员发信息可能出现的循环异常

    /**
     * 向工单系统发送信息异常处理
     */
    @ExceptionHandler(WosSysException.class)
    public void handler(WosSysException e) {

    }


    /**
     * #Description 非法参数异常处理
     *
     * @return 400错误
     * @author me
     * #Date 2022/6/19
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = IllegalArgumentException.class)
    public ResultWrapper handler(IllegalArgumentException e) {
        log.error("参数异常:-----------{}", e.getMessage());
        return ResultWrapper.fail(401, e.getMessage());
    }

    /**
     * #Description 通用运行异常处理
     *
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
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(value = {RuntimeException.class})
    public ResultWrapper handler02(RuntimeException e) {
        log.error("运行时异常:-----------{}", e.getMessage());
        e.printStackTrace();
        return ResultWrapper.fail(500, "服务器内部异常");
    }


    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(value = PortainerException.class)
    public ResultWrapper handler03(RuntimeException e) {
        // TODO: 完成portainer异常处理，异常加入队列中 (409重复创建应catch)
        log.error("Portainer异常:-----------{}", e.getMessage());
        e.printStackTrace();
        WosMessageDto message = new WosMessageDto();
        // TODO: 2022/7/25 完善异常信息
        message.setTitle("机炸了快来看");
        message.setDescription(e.getMessage());
        List<Long> operatorsWosIds = sysUserMapper.getOperatorsWosIds();
        operatorsWosIds.forEach(i -> {
            message.setReceiver(i);
            wosSysConnector.asyncSendMessage(message);
        });
        return ResultWrapper.fail("Portainer异常");
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = ApplyRejectException.class)
    public ResultWrapper handler04(RuntimeException e) {
        return ResultWrapper.fail(6000, e.getMessage());
    }


    /**
     * #Description 实体校验异常处理
     *
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
