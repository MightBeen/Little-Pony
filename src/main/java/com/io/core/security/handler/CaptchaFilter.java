package com.io.core.security.handler;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.io.core.common.exception.CaptchaException;
import com.io.core.common.wrapper.ResultWrapper;
import com.io.core.security.handler.LoginFailureHandler;
import com.io.core.common.utils.RedisUtil;
import com.io.core.common.wrapper.ConstValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class CaptchaFilter extends OncePerRequestFilter {

    @Autowired
    RedisUtil redisUtil;
    @Autowired
    LoginFailureHandler failureHandler;

    /**
     * #Description 校验验证码, 如果不正确则跳转至登录失败处理器 LoginFailureHandler
            * @return void
            * @author me
            * #Date 2022/6/19
    */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String url = request.getRequestURI();

        if ("/login".equals(url) && request.getMethod().equals("POST")) {

            try {
                validate(request);
            } catch (CaptchaException e) {
                failureHandler.onAuthenticationFailure(request, response, e);
            }
        }
        // 如过无异常发生则校验成功
        // 继续执行

        filterChain.doFilter(request, response);
    }

    // 执行校验逻辑
    private void validate(HttpServletRequest request) {

        String code = request.getParameter("code");
        String key = request.getParameter("token");

        if (StringUtils.isBlank(code) || StringUtils.isBlank(key)) {
            throw new CaptchaException("验证码错误");
        }

        if (!code.equals(redisUtil.hget(ConstValue.CAPTCHA_KEY, key))) {
            throw new CaptchaException("验证码错误");
        }

        redisUtil.hdel(ConstValue.CAPTCHA_KEY, key);
    }
}
