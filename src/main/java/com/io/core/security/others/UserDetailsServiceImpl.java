package com.io.core.security.others;

import com.io.core.common.entity.SysUser;
import com.io.core.service.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserDetailsServiceImpl implements UserDetailsService{

    @Autowired
    SysUserService userService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        SysUser user = userService.getByUsername(username);

        if (user == null) {
            // 用户名不存在
            throw new UsernameNotFoundException("用户名或密码不正确");
        }

        return new AccountUser(user, getUserAuth(user.getId()));
    }

    public List<GrantedAuthority> getUserAuth(long userId) {
        String authority = userService.getUserAuthInfo(userId);

        return AuthorityUtils.commaSeparatedStringToAuthorityList(authority);
    }
}
