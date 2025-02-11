package com.pard.root.security.entity;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Getter
public class CustomUserDetails implements UserDetails {
    private final String userId;
    private final String email;
    private final List<? extends GrantedAuthority> authorities;

    public CustomUserDetails(String userId, String email, Collection<? extends GrantedAuthority> roles) {
        this.userId = userId;
        this.email = email;
        this.authorities = roles.stream().toList();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public String getUsername() {  // userId를 username으로 사용
        return userId;
    }
}
