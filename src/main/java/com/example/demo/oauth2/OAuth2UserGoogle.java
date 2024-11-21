package com.example.demo.oauth2;

import java.util.Map;

public class OAuth2UserGoogle extends OAuth2UserDetails {

    public OAuth2UserGoogle(Map<String, Object> attributes) {
        super(attributes);
    }

    @Override
    public String getName() {
        return (String) attributes.get("name");
    }

    @Override
    public String getEmail() {
        return (String) attributes.get("email");
    }
}
