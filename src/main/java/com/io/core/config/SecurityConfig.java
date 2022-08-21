package com.io.core.config;


import com.io.core.security.handler.CaptchaFilter;
import com.io.core.security.handler.JwtAuthFilter;
import com.io.core.security.handler.JwtLogoutHandler;
import com.io.core.security.handler.LoginFailureHandler;
import com.io.core.security.handler.LoginSuccessHandler;
import com.io.core.security.handler.JwtAccessDeniedHandler;
import com.io.core.security.handler.JwtAuthEntryPoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


/**
 * Spring-Security相关配置
 * */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    /**
     * 验证码过滤器
     */
    @Autowired
    CaptchaFilter captchaFilter;

    @Autowired
    JwtAuthEntryPoint jwtAuthEntryPoint;


    /**
     * jwt认证错误处理器
     */
    @Autowired
    JwtAccessDeniedHandler jwtAccessDeniedHandler;


    /**
     * 用于获取数据库中用户信息
     */
    @Autowired
    UserDetailsService userDetailService;


    /**
     * 登录成功及失败处理器
     */
    @Autowired
    LoginFailureHandler loginFailureHandler;
    @Autowired
    LoginSuccessHandler loginSuccessHandler;

    /**
     * 登出处理器
     */
    @Autowired
    JwtLogoutHandler logoutSuccessHandler;


    /**
     * 白名单
     */
    private static final String[] URL_WHITELIST = {
            "/login",
            "/logout",
            "/captcha",
            "/favicon.ico",

            "/api/**",

            "/sys/setting/**"
    };


    /**
     * jwt 认证过滤器
     * */
    @Bean
    JwtAuthFilter jwtAuthFilter() throws Exception{
        return new JwtAuthFilter(authenticationManager());
    };


    /**
     * 数据库加密编码
     * @return
     */
    @Bean
    BCryptPasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }


    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.cors().
                and()
                .csrf().disable()

                //登陆设置
                .formLogin()
                .failureHandler(loginFailureHandler)
                .successHandler(loginSuccessHandler)

                // 登出设置
                .and()
                .logout()
                .logoutSuccessHandler(logoutSuccessHandler)

                .and()

                // 禁用session
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)

                // 配置拦截规则
                .and()
                .authorizeRequests()

                // 配置白名单
                .antMatchers(URL_WHITELIST).permitAll()
                .anyRequest().authenticated()

                // 异常处理器
                .and()
                .exceptionHandling()
                .authenticationEntryPoint(jwtAuthEntryPoint)
                .accessDeniedHandler(jwtAccessDeniedHandler)

                // 配置自定义过滤器
                .and()
                .addFilter(jwtAuthFilter())
                .addFilterBefore(captchaFilter, UsernamePasswordAuthenticationFilter.class);

    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailService);
    }
}
