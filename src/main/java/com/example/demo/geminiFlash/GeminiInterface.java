package com.example.demo.geminiFlash;


import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@RequestMapping("/v1beta/models/")
public interface GeminiInterface {
    @RequestMapping(method = RequestMethod.GET)
    JsonStructure.ModelList getModels();

    @RequestMapping(value = "{model}:countTokens", method = RequestMethod.POST)
    JsonStructure.GeminiCountResponse countTokens(
            @PathVariable("model") String model,
            @RequestBody JsonStructure.GeminiRequest request);

    @RequestMapping(value = "{model}:generateContent", method = RequestMethod.POST)
    JsonStructure.GeminiResponse getCompletion(
            @PathVariable("model") String model,
            @RequestBody JsonStructure.GeminiRequest request);
}
