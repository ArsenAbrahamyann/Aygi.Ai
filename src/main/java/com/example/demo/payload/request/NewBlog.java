package com.example.demo.payload.request;

import lombok.*;

import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NewBlog {
    @NotBlank
    private String title;
    @NotBlank
    private String text;


}
