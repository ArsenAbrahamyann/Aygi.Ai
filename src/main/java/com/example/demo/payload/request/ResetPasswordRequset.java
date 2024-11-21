package com.example.demo.payload.request;

import com.example.demo.annotations.ValidEmail;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
@Data
public class ResetPasswordRequset {
    @NotEmpty(message = "Code is required")
    private String code;
    @NotEmpty(message = "Password is required")
    @Size(min = 6)
    private String password;
    private String confirmPassword;

}
