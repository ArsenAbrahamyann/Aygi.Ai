package com.example.demo.geminiPro;

import com.example.demo.geminiFlash.GeminiResponseWithImage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GeminiServicePro {

    public static final String GEMINI_1_5_PRO = "gemini-1.5-pro";
    private final GeminiInterfacePro geminiInterfacePro;

    public GeminiResponseWithImage getCompletionWithImagePro(JsonStructurePro.GeminiRequest request) {
        return geminiInterfacePro.getCompletionWithImagePro(GEMINI_1_5_PRO, request);
    }

    public JsonStructurePro.GeminiResponse analyzeImagePro(JsonStructurePro.GeminiRequest request) {
        return geminiInterfacePro.analyzeImagePro(GEMINI_1_5_PRO, request);
    }

    public String getCompletionWithImagePro(String text, String imageFileName) throws IOException {
        Path imagePath = Path.of("src/main/resources/", imageFileName);
        String base64Image = Base64.getEncoder().encodeToString(Files.readAllBytes(imagePath));

        JsonStructurePro.GeminiRequest request = new JsonStructurePro.GeminiRequest(
                List.of(new JsonStructurePro.Content(List.of(
                        new JsonStructurePro.TextPart(text),
                        new JsonStructurePro.InlineDataPart(new JsonStructurePro.InlineData("image/png", base64Image))
                ))));

        GeminiResponseWithImage response = getCompletionWithImagePro(request);
        return response.getGeminiResponse().getCandidates().get(0).getContent().get().getParts().get(0).toString();
    }

    public String analyzeImagePro(String text, String imageFileName) throws IOException {
        Path imagePath = Path.of("src/main/resources/", imageFileName);
        String base64Image = Base64.getEncoder().encodeToString(Files.readAllBytes(imagePath));

        JsonStructurePro.GeminiRequest request = new JsonStructurePro.GeminiRequest(
                List.of(new JsonStructurePro.Content(List.of(
                        new JsonStructurePro.TextPart(text),
                        new JsonStructurePro.InlineDataPart(new JsonStructurePro.InlineData("image/png", base64Image))
                ))));

        JsonStructurePro.GeminiResponse response = analyzeImagePro(request);
        return response.getCandidates().get(0).getContent().get().getParts().get(0).toString();
    }
}
