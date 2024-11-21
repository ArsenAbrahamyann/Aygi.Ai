package com.example.demo.geminiPro;


import com.example.demo.geminiFlash.GeminiInterface;
import com.example.demo.geminiFlash.GeminiInterfaceImpl;
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
@Profile("pro")
public class AppConfigPro {

    @Value("${gemini.base-url}")
    private String baseUrl;

    @Value("${googleai.api.key}")
    private String apiKey;

    @Bean(name = "geminiRestTemplatePro")
    public RestTemplate geminiRestTemplatePro() {
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