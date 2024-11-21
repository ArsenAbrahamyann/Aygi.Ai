package com.example.demo.oauth2.security;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.List;
import java.util.Map;

@Getter
@RequiredArgsConstructor
public class OAuth2UserDetailCustom implements OAuth2User, UserDetails {


    private final Integer id;
    private final String username;
    private final String password;
    private final List<GrantedAuthority> authorities;
    private Map<String, Object> attributes;

    private boolean isEnabled;
    private boolean accountNonExpired;
    private boolean accountNonLocked;
    private boolean credentialsNonExpired;

    @Override
    public String getName() {
        return String.valueOf(this.id);
    }
}
