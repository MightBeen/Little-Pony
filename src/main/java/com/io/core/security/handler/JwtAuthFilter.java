package com.io.core.security.handler;

import cn.hutool.core.util.StrUtil;
import com.io.core.common.utils.JwtUtils;
import com.io.core.security.others.UserDetailsServiceImpl;
import com.io.core.service.SysUserService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
public class JwtAuthFilter extends BasicAuthenticationFilter {

    @Autowired
    JwtUtils jwtUtils;

    @Autowired
    UserDetailsServiceImpl userDetailsService;

    @Autowired
    SysUserService userService;

    public JwtAuthFilter(AuthenticationManager authenticationManager) {
        super(authenticationManager);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {

        String jwt = request.getHeader(jwtUtils.getHeader());

        if (StrUtil.isBlankOrUndefined(jwt)) {
            chain.doFilter(request, response);
            return;
        }

        Claims claim = null;
        try {
            claim = jwtUtils.getClaimByToken(jwt);
        } catch (JwtException e) {
            chain.doFilter(request, response);
            return;
        }

        if (claim == null) {
            throw new JwtException("token 异常");
        }

        String username = claim.getSubject();

        // 获取用户的权限信息

        UsernamePasswordAuthenticationToken token = null;
        try {
            token = new UsernamePasswordAuthenticationToken(username, null, userDetailsService.getUserAuth(userService.getByUsername(username).getId()));
        } catch (NullPointerException e) {
            log.warn("======= 获取权限异常，方法调用错误 =========");
        }

        SecurityContextHolder.getContext().setAuthentication(token);

        chain.doFilter(request, response);
    }
}
