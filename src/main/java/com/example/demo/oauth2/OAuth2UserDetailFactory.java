package com.example.demo.oauth2;

import java.util.Map;

public class OAuth2UserDetailFactory {
    public static OAuth2UserDetails getOAuth2UserDetail(Map<String, Object> attributes) {
        return new OAuth2UserGoogle(attributes);
    }
}
