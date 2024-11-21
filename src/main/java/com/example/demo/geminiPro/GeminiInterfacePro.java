package com.example.demo.geminiPro;

import com.example.demo.geminiFlash.GeminiResponseWithImage;
import com.example.demo.geminiFlash.JsonStructure;

public interface GeminiInterfacePro {
    GeminiResponseWithImage getCompletionWithImagePro(String model, JsonStructurePro.GeminiRequest request);
    JsonStructurePro.GeminiResponse analyzeImagePro(String model, JsonStructurePro.GeminiRequest request);
}
