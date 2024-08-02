package com.oauth.authorization.domain.user.model;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
public class UserInfoAdapter extends User {

    private static final long serialVersionUID = 1L;
    private UserInfo userInfo;

    public UserInfoAdapter(UserInfo userInfo) {
        super(userInfo.getUserId(), userInfo.getPassword(), authorities(userInfo.getRole()));
        this.userInfo = userInfo;
    }

    private static Collection<? extends GrantedAuthority> authorities(Set<UserRole> roles) {
        return roles.stream().map(role -> new SimpleGrantedAuthority(role.name())).collect(Collectors.toSet());
    }
}
