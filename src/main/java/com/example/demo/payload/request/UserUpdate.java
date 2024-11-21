package com.example.demo.payload.request;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class UserUpdate {
    @NotEmpty
    private String username;
    private String bio;
}
