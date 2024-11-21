package com.example.demo.geminiFlash;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;

@Configuration
@ComponentScan("com.example.demo.geminiFlash")
@Profile("flash")
public class AppConfig {

    @Value("${gemini.base-url}")
    private String baseUrl;

    @Value("${googleai.api.key}")
    private String apiKey;

    @Bean
    public RestTemplate geminiRestTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        // Set headers
        HttpHeaders headers = new HttpHeaders();
        headers.add("x-goog-api-key", apiKey);
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        restTemplate.setInterceptors(Collections.singletonList((request, body, execution) -> {
            request.getHeaders().addAll(headers);
            return execution.execute(request, body);
        }));

        return restTemplate;

    }

    @Bean
    public GeminiInterface geminiInterface(RestTemplate restTemplate) {
        return new GeminiInterfaceImpl(baseUrl, restTemplate);
    }

}