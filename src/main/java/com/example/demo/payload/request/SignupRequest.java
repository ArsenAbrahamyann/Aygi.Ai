package com.example.demo.payload.request;

import com.example.demo.annotations.PasswordMatches;
import com.example.demo.annotations.ValidEmail;
import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.*;

@Data
@AllArgsConstructor
@PasswordMatches
public class SignupRequest {

    @NotBlank(message = "Email can not be blank")
    @Email(message = "Email should be valid")
    @Size(max = 100, message = "Email length should be at most 100 characters")
    private String email;

    @NotBlank(message = "userName can not be blank")
    @Pattern(regexp = "^[A-Za-z][\\p{L}0-9 .'-]*$", message = "Username should start with a letter and contain only letters, digits, spaces, apostrophes, hyphens, and dots")
    @Size(min = 2, max = 50, message = "userName length should be between 2 and 50 characters")
    private String username;
//    private String confirmPassword;

    @NotBlank(message = "Password cannot be blank")
    @Size(min = 6, message = "Password should have at least 6 characters")
    private String password;
}

