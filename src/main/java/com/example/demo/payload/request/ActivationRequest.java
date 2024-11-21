package com.example.demo.payload.request;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class ActivationRequest {
    @NotEmpty(message = "Code cannot be empty")
    private String code;

}

