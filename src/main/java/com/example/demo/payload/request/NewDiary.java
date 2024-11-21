package com.example.demo.payload.request;


import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import javax.validation.constraints.NotEmpty;

@Data
@ToString
@RequiredArgsConstructor
public class NewDiary {
    @NotEmpty
    private final String name;
    @NotEmpty
    private final String about;
    private boolean isPublic;
}
