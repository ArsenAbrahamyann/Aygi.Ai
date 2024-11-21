package com.example.demo.geminiFlash;

import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;
import java.util.List;

import com.example.demo.geminiFlash.JsonStructure.*;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GeminiService {
    public static final String GEMINI_1_5_FLASH = "gemini-1.5-flash";
    private final GeminiInterface geminiInterface;

    public ModelList getModels() {
        return geminiInterface.getModels();
    }

    public GeminiCountResponse countTokens(String model, GeminiRequest request) {
        return geminiInterface.countTokens(model, request);
    }

    public int countTokens(String text) {
        GeminiCountResponse response = countTokens(GEMINI_1_5_FLASH, new GeminiRequest(
                List.of(new Content(List.of(new TextPart(text))))));
        return response.getTotalTokens();
    }

    public GeminiResponse getCompletion(GeminiRequest request) {
        return geminiInterface.getCompletion(GEMINI_1_5_FLASH, request);
    }

    public GeminiResponse getCompletionWithModel(String model, GeminiRequest request) {
        return geminiInterface.getCompletion(model, request);
    }

    public GeminiResponse getCompletionWithImage(GeminiRequest request) {
        return geminiInterface.getCompletion(GEMINI_1_5_FLASH, request);
    }

    public GeminiResponse analyzeImage(GeminiRequest request) {
        return geminiInterface.getCompletion(GEMINI_1_5_FLASH, request);
    }

    public String getCompletion(String text) {
        GeminiResponse response = getCompletion(new GeminiRequest(
                List.of(new Content(List.of(new TextPart(text))))));
        return response.getCandidates().get(0).getContent().get().getParts().get(0).toString();
    }

    public String getCompletionWithImage(String text, String imageFileName) throws IOException {
        GeminiResponse response = getCompletionWithImage(
                new GeminiRequest(List.of(new Content(List.of(
                        new TextPart(text),
                        new InlineDataPart(new InlineData("image/png",
                                Base64.getEncoder().encodeToString(Files.readAllBytes(
                                        Path.of("src/main/resources/", imageFileName))))))
                ))));
        return response.getCandidates().get(0).getContent().get().getParts().get(0).toString();
    }

    public String analyzeImage(String text, String imageFileName) throws IOException {
        GeminiResponse response = analyzeImage(
                new GeminiRequest(List.of(new Content(List.of(
                        new TextPart(text),
                        new InlineDataPart(new InlineData("image/png",
                                Base64.getEncoder().encodeToString(Files.readAllBytes(
                                        Path.of("src/main/resources/", imageFileName))))))
                ))));
        return response.getCandidates().get(0).getContent().get().getParts().get(0).toString();
    }
}
