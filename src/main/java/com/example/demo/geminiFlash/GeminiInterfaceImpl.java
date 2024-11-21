package com.example.demo.geminiFlash;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.RestTemplate;

public class GeminiInterfaceImpl implements GeminiInterface {

    private final String baseUrl;
    private final RestTemplate restTemplate;


    @Autowired
    public GeminiInterfaceImpl(String baseUrl, RestTemplate restTemplate) {
        this.baseUrl = baseUrl;
        this.restTemplate = restTemplate;
    }

    @Override
    public JsonStructure.ModelList getModels() {
        String url = baseUrl + "/v1beta/models/";
        return restTemplate.getForObject(url, JsonStructure.ModelList.class);
    }

    @Override
    public JsonStructure.GeminiCountResponse countTokens(String model, JsonStructure.GeminiRequest request) {
        String url = baseUrl + "/v1beta/models/" + model + ":countTokens";
        return restTemplate.postForObject(url, request, JsonStructure.GeminiCountResponse.class);
    }

    @Override
    public JsonStructure.GeminiResponse getCompletion(String model, JsonStructure.GeminiRequest request) {
        String url = baseUrl + "/v1beta/models/" + model + ":generateContent";
        return restTemplate.postForObject(url, request, JsonStructure.GeminiResponse.class);
    }
}