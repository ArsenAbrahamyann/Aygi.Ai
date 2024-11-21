package com.example.demo.geminiFlash;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GeminiResponseWithImage {
    private JsonStructure.GeminiResponse geminiResponse;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String uploadedImageBase64; // Base64 encoded image data

}
