package com.example.demo.security;

public class SecurityConstants {

    public static final String AUTH_SERVICE = "/api/auth";

    public static final String SECRET = "SecretKeyGenJWT";
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String HEADER_STRING = "Authorization";
    public static final String CONTENT_TYPE = "application/json";
    public static final long EXPIRATION_TIME = 600_000_000; //10min

}
