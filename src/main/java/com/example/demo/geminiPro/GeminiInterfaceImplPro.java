package com.example.demo.geminiPro;

import com.example.demo.geminiFlash.GeminiResponseWithImage;
import com.example.demo.geminiFlash.JsonStructure;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
@Component
public class GeminiInterfaceImplPro implements GeminiInterfacePro {
    private final String baseUrl;
    @Autowired
    @Qualifier("geminiRestTemplatePro")
    private final RestTemplate restTemplate;

    public GeminiInterfaceImplPro(@Value("${gemini.base-url}") String baseUrl, RestTemplate restTemplate) {
        this.baseUrl = baseUrl;
        this.restTemplate = restTemplate;
    }

    @Override
    public GeminiResponseWithImage getCompletionWithImagePro(String model, JsonStructurePro.GeminiRequest request) {
        String url = baseUrl + "/v1beta/models/" + model + ":generateContentWithImage";
        // Assuming the response includes both GeminiResponse and image data
        // Implement the actual logic to handle response accordingly
        return restTemplate.postForObject(url, request, GeminiResponseWithImage.class);
    }

    @Override
    public JsonStructurePro.GeminiResponse analyzeImagePro(String model, JsonStructurePro.GeminiRequest request) {
        String url = baseUrl + "/v1beta/models/" + model + ":analyzeImage";
        return restTemplate.postForObject(url, request, JsonStructurePro.GeminiResponse.class);
    }
}
