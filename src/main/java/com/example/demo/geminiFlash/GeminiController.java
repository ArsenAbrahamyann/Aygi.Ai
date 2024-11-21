package com.example.demo.geminiFlash;

import java.io.IOException;
import java.util.Base64;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/auth/gemini")
@RequiredArgsConstructor
@Tag(name = "Gemini Controller", description = "Endpoints for Gemini operations")
public class GeminiController {
    private static final String IMAGE_PROMPT = "Now I will upload a photo of the plant, and you will identify the type of plant it is. Your response should be at least 1500 words long. Try to write unique, SEO, plagiarism-free text. Aim to write like a human, not as if it's AI-generated.\n"
            +
            "\n"
            +
            "If I upload a photo that does not depict a plant , answer with: \"I am an AI assistant at Aygi.ai, and I can only provide information about plants. The photo does not show a plant.\"\n"
            +
            "\n"
            +
            "If the photo shows not a photograph but a drawing of a plant, respond with: \"Please upload a photograph of the plant.\"\n"
            +
            "\n"
            +
            "Your answer should be formatted as follows:\n"
            +
            "\n"
            +
            "Plant Category: (write the plant category here)\n"
            +
            "\n"
            +
            "Plant Name: (write the name of the plant here)\n"
            +
            "\n"
            +
            "About the Plant:\n"
            +
            "(Include detailed information about the plant here.)\n"
            +
            "\n"
            +
            "How to Care for the Plant:\n"
            +
            "(Include detailed instructions on how to care for the plant here.)\n"
            +
            "\n"
            +
            "In conclusion, mention that you are just an AI assistant at Aygi.ai and cannot guarantee an accurate answer, recommending consultation with specialists for precise information. ";
    private static final String IMAGE_PROMPT_AM = "Now I will upload a photo of the plant, and you will identify the type of plant it is. Your response should be at least 1500 words long. Try to write unique, SEO, plagiarism-free text. Aim to write like a human, not as if it's AI-generated.  \n"
            +
            "\n"
            +
            "If I upload a photo that does not depict a plant , answer with: \"I am an AI assistant at Aygi.ai, and I can only provide information about plants. The photo does not show a plant.\"\n"
            +
            "\n"
            +
            "If the photo shows not a photograph but a drawing of a plant, respond with: \"Please upload a photograph of the plant.\"\n"
            +
            "\n"
            +
            "Your answer should be formatted as follows:\n"
            +
            "\n"
            +
            "Plant Category: (write the plant category here)\n"
            +
            "\n"
            +
            "Plant Name: (write the name of the plant here)\n"
            +
            "\n"
            +
            "About the Plant:\n"
            +
            "(Include detailed information about the plant here.)\n"
            +
            "\n"
            +
            "How to Care for the Plant:\n"
            +
            "(Include detailed instructions on how to care for the plant here.)\n"
            +
            "\n"
            +
            "In conclusion, mention that you are just an AI assistant at Aygi.ai and cannot guarantee an accurate answer, recommending consultation with specialists for precise information.  YOUR ANSWER MUST BE IN ARMENIAN ONLY!";

    private static final String IMAGE_PROMPT1 = "Now I will upload a photo of the plant, and you will identify the type of plant it is. Your response should be at least 1500 words long. Try to write unique, SEO, plagiarism-free text. Aim to write like a human, not as if it's AI-generated.\n"
            +
            "If you find insects or larvae in the photo I upload, write down the name of the insect or larva and explain how to treat the plant for them.\n"
            +
            "If I upload a photo that does not depict a plant , answer with: \"I am an AI assistant at Aygi.ai, and I can only provide information about plants. The photo does not show a plant.\"\n"
            +
            "If the photo shows not a photograph but a drawing of a plant, respond with: \"Please upload a photograph of the plant.\"\n"
            +
            "Your answer should be formatted as follows:\n"
            +
            "Plant Category: (write the plant category here)\n"
            +
            "Plant Name: (write the name of the plant here)\n"
            +
            "About the Plant Disease:\n"
            +
            "(Write here about the disease affecting the plant. If the plant is healthy, state here that it is healthy.)\n"
            +
            "About Treatment:\n"
            +
            "(Write here about how to treat the plant. If the plant is healthy, describe its condition here and explain how to care for it.)\n"
            +
            "In conclusion, mention that you are just an AI assistant at Aygi.ai and cannot guarantee an accurate answer, recommending consultation with specialists for precise information.  ";

    private static final String IMAGE_PROMPT1_AM = "Now I will upload a photo of the plant, and you will identify the type of plant it is. Your response should be at least 1500 words long. Try to write unique, SEO, plagiarism-free text. Aim to write like a human, not as if it's AI-generated. \n"
            +
            "If you find insects or larvae in the photo I upload, write down the name of the insect or larva and explain how to treat the plant for them.\n"
            +
            "If I upload a photo that does not depict a plant , answer with: \"I am an AI assistant at Aygi.ai, and I can only provide information about plants. The photo does not show a plant.\"\n"
            +
            "If the photo shows not a photograph but a drawing of a plant, respond with: \"Please upload a photograph of the plant.\"\n"
            +
            "Your answer should be formatted as follows:\n"
            +
            "Plant Category: (write the plant category here)\n"
            +
            "Plant Name: (write the name of the plant here)\n"
            +
            "About the Plant Disease:\n"
            +
            "(Write here about the disease affecting the plant. If the plant is healthy, state here that it is healthy.)\n"
            +
            "About Treatment:\n"
            +
            "(Write here about how to treat the plant. If the plant is healthy, describe its condition here and explain how to care for it.)\n"
            +
            "In conclusion, mention that you are just an AI assistant at Aygi.ai and cannot guarantee an accurate answer, recommending consultation with specialists for precise information.  YOUR ANSWER MUST BE IN ARMENIAN ONLY!";

    private static final String IMAGE_PROMPT_WEED_DETECTION = "Now I will upload a photo of the plant, and you will identify whether it is a parasite or a weed. Your response should be at least 1500 words long. Try to write unique, SEO, plagiarism-free text. Aim to write like a human, not as if it's AI-generated.\n"
            +
            "\n"
            +
            "If I upload a photo that does not depict a plant , answer with: \"I am an AI assistant at Aygi.ai, and I can only provide information about plants. The photo does not show a plant.\"\n"
            +
            "\n"
            +
            "If the photo shows not a photograph but a drawing of a plant, respond with: \"Please upload a photograph of the plant.\"\n"
            +
            "\n"
            +
            "Your answer should be formatted as follows:\n"
            +
            "\n"
            +
            "Plant Category: (write the plant category here)\n"
            +
            "\n"
            +
            "Plant Name: (write the name of the plant here)\n"
            +
            "\n"
            +
            "About the Plant:\n"
            +
            "(If it is a parasite or a weed, provide the name of the weed or parasite. If it is neither, indicate that the plant is harmless and provide some information about it)\n"
            +
            "\n"
            +
            "How to get rid of weed:\n"
            +
            "(If it is a weed or a parasite, tell me how to manage it)\n"
            +
            "\n"
            +
            "In conclusion, mention that you are just an AI assistant at Aygi.ai and cannot guarantee an accurate answer, recommending consultation with specialists for precise information. ";
    private static final String IMAGE_PROMPT_WEED_DETECTION_AM = "Now I will upload a photo of the plant, and you will identify whether it is a parasite or a weed. Your response should be at least 1500 words long. Try to write unique, SEO, plagiarism-free text. Aim to write like a human, not as if it's AI-generated. \n"
            +
            "\n"
            +
            "If I upload a photo that does not depict a plant , answer with: \"I am an AI assistant at Aygi.ai, and I can only provide information about plants. The photo does not show a plant.\"\n"
            +
            "\n"
            +
            "If the photo shows not a photograph but a drawing of a plant, respond with: \"Please upload a photograph of the plant.\"\n"
            +
            "\n"
            +
            "Your answer should be formatted as follows:\n"
            +
            "\n"
            +
            "Plant Category: (write the plant category here)\n"
            +
            "\n"
            +
            "Plant Name: (write the name of the plant here)\n"
            +
            "\n"
            +
            "About the Plant:\n"
            +
            "(If it is a parasite or a weed, provide the name of the weed or parasite. If it is neither, indicate that the plant is harmless and provide some information about it)\n"
            +
            "\n"
            +
            "How to get rid of weed:\n"
            +
            "(If it is a weed or a parasite, tell me how to manage it)\n"
            +
            "\n"
            +
            "In conclusion, mention that you are just an AI assistant at Aygi.ai and cannot guarantee an accurate answer, recommending consultation with specialists for precise information.  YOUR ANSWER MUST BE IN ARMENIAN ONLY!";

    private static final String IMAGE_PROMPT_PEST_DETECTION = "Now I will upload a photo of the plant, and you will identify the type of plant it is. Your response should be at least 1500 words long. Try to write unique, SEO, plagiarism-free text. Aim to write like a human, not as if it's AI-generated.\n"
            +
            "\n"
            +
            "If you find insects or larvae in the photo I upload, write down the name of the insect or larva and explain how to treat the plant for them.\n"
            +
            "\n"
            +
            "If I upload a photo that does not depict a plant , answer with: \"I am an AI assistant at Aygi.ai, and I can only provide information about plants. The photo does not show a plant.\"\n"
            +
            "\n"
            +
            "If the photo shows not a photograph but a drawing of a plant, respond with: \"Please upload a photograph of the plant.\"\n"
            +
            "\n"
            +
            "Your answer should be formatted as follows:\n"
            +
            "\n"
            +
            "Plant Category: (write the plant category here)\n"
            +
            "\n"
            +
            "Plant Name: (write the name of the plant here)\n"
            +
            "\n"
            +
            "About the Plant Disease:\n"
            +
            "(Write here about the disease affecting the plant. If you find insects or larvae in the photo I upload, write down the name of the insect or larva and explain how to treat the plant for them. If the plant is healthy, state here that it is healthy.)\n"
            +
            "\n"
            +
            "About Treatment:\n"
            +
            "(Write here about how to treat the plant. If the plant is healthy, describe its condition here and explain how to care for it.)\n"
            +
            "\n"
            +
            "In conclusion, mention that you are just an AI assistant at Aygi.ai and cannot guarantee an accurate answer, recommending consultation with specialists for precise information. ";
    private static final String IMAGE_PROMPT_PEST_DETECTION_AM = "Now I will upload a photo of the plant, and you will identify the type of plant it is. Your response should be at least 1500 words long. Try to write unique, SEO, plagiarism-free text. Aim to write like a human, not as if it's AI-generated. \n"
            +
            "\n"
            +
            "If you find insects or larvae in the photo I upload, write down the name of the insect or larva and explain how to treat the plant for them.\n"
            +
            "\n"
            +
            "If I upload a photo that does not depict a plant , answer with: \"I am an AI assistant at Aygi.ai, and I can only provide information about plants. The photo does not show a plant.\"\n"
            +
            "\n"
            +
            "If the photo shows not a photograph but a drawing of a plant, respond with: \"Please upload a photograph of the plant.\"\n"
            +
            "\n"
            +
            "Your answer should be formatted as follows:\n"
            +
            "\n"
            +
            "Plant Category: (write the plant category here)\n"
            +
            "\n"
            +
            "Plant Name: (write the name of the plant here)\n"
            +
            "\n"
            +
            "About the Plant Disease:\n"
            +
            "(Write here about the disease affecting the plant. If you find insects or larvae in the photo I upload, write down the name of the insect or larva and explain how to treat the plant for them. If the plant is healthy, state here that it is healthy.)\n"
            +
            "\n"
            +
            "About Treatment:\n"
            +
            "(Write here about how to treat the plant. If the plant is healthy, describe its condition here and explain how to care for it.)\n"
            +
            "\n"
            +
            "In conclusion, mention that you are just an AI assistant at Aygi.ai and cannot guarantee an accurate answer, recommending consultation with specialists for precise information.  YOUR ANSWER MUST BE IN ARMENIAN ONLY!";
    private final GeminiService geminiService;

    @Operation(summary = "Get Gemini Response",
            description = "Get Gemini Response based on the text request",
            security = {@SecurityRequirement(name = "bearerAuth")})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved Gemini Response"),
            @ApiResponse(responseCode = "400", description = "Bad Request"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    @PostMapping("/")
    public JsonStructure.GeminiResponse getResponse(@RequestBody JsonStructure.GeminiRequest request) {
        return geminiService.getCompletion(request);
    }

    @PostMapping(value = "/weedDetectionImage", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    @Operation(summary = "Get Gemini Response with Image for weed Detection",
            description = "Get Gemini Response based on image file for detecting weed",
            security = {@SecurityRequirement(name = "bearerAuth")})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved Gemini Response"),
            @ApiResponse(responseCode = "400", description = "Bad Request"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    public GeminiResponseWithImage getCompletionWithDetectWaterStress(@RequestParam("file") MultipartFile file) {
        try {
            String base64Image = Base64.getEncoder().encodeToString(file.getBytes());

            JsonStructure.GeminiRequest request = new JsonStructure.GeminiRequest(
                    List.of(new JsonStructure.Content(
                            List.of(new JsonStructure.TextPart(IMAGE_PROMPT_WEED_DETECTION),
                                    new JsonStructure.InlineDataPart(
                                            new JsonStructure.InlineData("image/jpeg", base64Image)))
                    ))
            );

            JsonStructure.GeminiResponse response = geminiService.getCompletionWithImage(request);
            byte[] fileBytes = file.getBytes();
            // Encode the bytes to Base64
            String uploadedImageBase64 = Base64.getEncoder().encodeToString(fileBytes);
            return new GeminiResponseWithImage(response, uploadedImageBase64);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    @PostMapping(value = "/weedDetectionImageAm", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    @Operation(summary = "Get Gemini Response with Image for weed Detection",
            description = "Get Gemini Response based on image file for detecting weed",
            security = {@SecurityRequirement(name = "bearerAuth")})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved Gemini Response"),
            @ApiResponse(responseCode = "400", description = "Bad Request"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    public GeminiResponseWithImage getCompletionWithDetectWaterStressAm(@RequestParam("file") MultipartFile file) {
        try {
            String base64Image = Base64.getEncoder().encodeToString(file.getBytes());

            JsonStructure.GeminiRequest request = new JsonStructure.GeminiRequest(
                    List.of(new JsonStructure.Content(
                            List.of(new JsonStructure.TextPart(IMAGE_PROMPT_WEED_DETECTION_AM),
                                    new JsonStructure.InlineDataPart(
                                            new JsonStructure.InlineData("image/jpeg", base64Image)))
                    ))
            );

            JsonStructure.GeminiResponse response = geminiService.getCompletionWithImage(request);
            byte[] fileBytes = file.getBytes();
            // Encode the bytes to Base64
            String uploadedImageBase64 = Base64.getEncoder().encodeToString(fileBytes);
            return new GeminiResponseWithImage(response, uploadedImageBase64);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @PostMapping(value = "/pestDetectionImage", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    @Operation(summary = "Get Gemini Response with Image for pest Fertilizer Advice",
            description = "Get Gemini Response based on image file for pest fertilizer advice",
            security = {@SecurityRequirement(name = "bearerAuth")})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved Gemini Response"),
            @ApiResponse(responseCode = "400", description = "Bad Request"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    public GeminiResponseWithImage getCompletionWithOrganicFertilizerAdvice(@RequestParam("file") MultipartFile file) {
        try {
            String base64Image = Base64.getEncoder().encodeToString(file.getBytes());

            JsonStructure.GeminiRequest request = new JsonStructure.GeminiRequest(
                    List.of(new JsonStructure.Content(
                            List.of(new JsonStructure.TextPart(IMAGE_PROMPT_PEST_DETECTION),
                                    new JsonStructure.InlineDataPart(
                                            new JsonStructure.InlineData("image/jpeg", base64Image)))
                    ))
            );

            JsonStructure.GeminiResponse response = geminiService.getCompletionWithImage(request);
            byte[] fileBytes = file.getBytes();
            // Encode the bytes to Base64
            String uploadedImageBase64 = Base64.getEncoder().encodeToString(fileBytes);
            return new GeminiResponseWithImage(response, uploadedImageBase64);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    @PostMapping(value = "/pestDetectionImageAm", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    @Operation(summary = "Get Gemini Response with Image for pest Fertilizer Advice",
            description = "Get Gemini Response based on image file for pest fertilizer advice",
            security = {@SecurityRequirement(name = "bearerAuth")})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved Gemini Response"),
            @ApiResponse(responseCode = "400", description = "Bad Request"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    public GeminiResponseWithImage getCompletionWithOrganicFertilizerAdviceAm(@RequestParam("file") MultipartFile file) {
        try {
            String base64Image = Base64.getEncoder().encodeToString(file.getBytes());

            JsonStructure.GeminiRequest request = new JsonStructure.GeminiRequest(
                    List.of(new JsonStructure.Content(
                            List.of(new JsonStructure.TextPart(IMAGE_PROMPT_PEST_DETECTION_AM),
                                    new JsonStructure.InlineDataPart(
                                            new JsonStructure.InlineData("image/jpeg", base64Image)))
                    ))
            );

            JsonStructure.GeminiResponse response = geminiService.getCompletionWithImage(request);
            byte[] fileBytes = file.getBytes();
            // Encode the bytes to Base64
            String uploadedImageBase64 = Base64.getEncoder().encodeToString(fileBytes);
            return new GeminiResponseWithImage(response, uploadedImageBase64);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @PostMapping(value = "/image", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    @Operation(summary = "Get Gemini Response with Image",
            description = "Get Gemini Response based on image file",
            security = {@SecurityRequirement(name = "bearerAuth")})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved Gemini Response"),
            @ApiResponse(responseCode = "400", description = "Bad Request"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    public GeminiResponseWithImage getCompletionWithImage(
//            @RequestParam("text") String text,
            @RequestParam("file") MultipartFile file
    ) {
        try {
            String base64Image = Base64.getEncoder().encodeToString(file.getBytes());

            JsonStructure.GeminiRequest request = new JsonStructure.GeminiRequest(
                    List.of(new JsonStructure.Content(
                            List.of(new JsonStructure.TextPart(IMAGE_PROMPT),
                                    new JsonStructure.InlineDataPart(
                                            new JsonStructure.InlineData("image/jpeg", base64Image)))
                    ))
            );

            JsonStructure.GeminiResponse response = geminiService.getCompletionWithImage(request);
            byte[] fileBytes = file.getBytes();
            // Encode the bytes to Base64
            String uploadedImageBase64 = Base64.getEncoder().encodeToString(fileBytes);
            return new GeminiResponseWithImage(response, uploadedImageBase64);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    @PostMapping(value = "/imageAm", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    @Operation(summary = "Get Gemini Response with Image",
            description = "Get Gemini Response based on image file",
            security = {@SecurityRequirement(name = "bearerAuth")})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved Gemini Response"),
            @ApiResponse(responseCode = "400", description = "Bad Request"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    public GeminiResponseWithImage getCompletionWithImageAm(
//            @RequestParam("text") String text,
            @RequestParam("file") MultipartFile file
    ) {
        try {
            String base64Image = Base64.getEncoder().encodeToString(file.getBytes());

            JsonStructure.GeminiRequest request = new JsonStructure.GeminiRequest(
                    List.of(new JsonStructure.Content(
                            List.of(new JsonStructure.TextPart(IMAGE_PROMPT_AM),
                                    new JsonStructure.InlineDataPart(
                                            new JsonStructure.InlineData("image/jpeg", base64Image)))
                    ))
            );

            JsonStructure.GeminiResponse response = geminiService.getCompletionWithImage(request);
            byte[] fileBytes = file.getBytes();
            // Encode the bytes to Base64
            String uploadedImageBase64 = Base64.getEncoder().encodeToString(fileBytes);
            return new GeminiResponseWithImage(response, uploadedImageBase64);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @PostMapping(value = "/diseaseImage", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    @Operation(summary = "Get Gemini Response with Image",
            description = "Get Gemini Response for disease based on image file",
            security = {@SecurityRequirement(name = "bearerAuth")})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved Gemini Response"),
            @ApiResponse(responseCode = "400", description = "Bad Request"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    public GeminiResponseWithImage getCompletionWithImageDisease(
//            @RequestParam("text") String text,
            @RequestParam("file") MultipartFile file
    ) {
        try {
            String base64Image = Base64.getEncoder().encodeToString(file.getBytes());

            JsonStructure.GeminiRequest request = new JsonStructure.GeminiRequest(
                    List.of(new JsonStructure.Content(
                            List.of(new JsonStructure.TextPart(IMAGE_PROMPT1),
                                    new JsonStructure.InlineDataPart(
                                            new JsonStructure.InlineData("image/jpeg", base64Image)))
                    ))
            );

            JsonStructure.GeminiResponse response = geminiService.getCompletionWithImage(request);
            byte[] fileBytes = file.getBytes();
            // Encode the bytes to Base64
            String uploadedImageBase64 = Base64.getEncoder().encodeToString(fileBytes);
            return new GeminiResponseWithImage(response, uploadedImageBase64);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    @PostMapping(value = "/diseaseImageAm", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    @Operation(summary = "Get Gemini Response with Image",
            description = "Get Gemini Response for disease based on image file",
            security = {@SecurityRequirement(name = "bearerAuth")})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved Gemini Response"),
            @ApiResponse(responseCode = "400", description = "Bad Request"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    public GeminiResponseWithImage getCompletionWithImageDiseaseAm(
//            @RequestParam("text") String text,
            @RequestParam("file") MultipartFile file
    ) {
        try {
            String base64Image = Base64.getEncoder().encodeToString(file.getBytes());

            JsonStructure.GeminiRequest request = new JsonStructure.GeminiRequest(
                    List.of(new JsonStructure.Content(
                            List.of(new JsonStructure.TextPart(IMAGE_PROMPT1_AM),
                                    new JsonStructure.InlineDataPart(
                                            new JsonStructure.InlineData("image/jpeg", base64Image)))
                    ))
            );

            JsonStructure.GeminiResponse response = geminiService.getCompletionWithImage(request);
            byte[] fileBytes = file.getBytes();
            // Encode the bytes to Base64
            String uploadedImageBase64 = Base64.getEncoder().encodeToString(fileBytes);
            return new GeminiResponseWithImage(response, uploadedImageBase64);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


}