package com.io.core.security.handler;

import com.io.core.common.exception.CaptchaException;
import com.io.core.common.wrapper.ResultWrapper;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class LoginFailureHandler implements AuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        String msg = exception instanceof CaptchaException ? exception.getMessage() : "用户名或密码错误";
        ResultWrapper.writeMsg(response, msg, false);
    }
}
