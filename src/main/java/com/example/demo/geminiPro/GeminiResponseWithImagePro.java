package com.example.demo.geminiPro;

import com.fasterxml.jackson.annotation.JsonInclude;

public class GeminiResponseWithImagePro {
    private JsonStructurePro.GeminiResponse geminiResponse;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String uploadedImageBase64; // Base64 encoded image data



}
