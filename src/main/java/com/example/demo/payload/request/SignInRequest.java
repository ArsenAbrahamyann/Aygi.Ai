package com.example.demo.payload.request;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class SignInRequest {

    @NotEmpty(message = "email cannot be empty")
    private String email;
    @NotEmpty(message = "Password cannot be empty")
    private String password;

}
