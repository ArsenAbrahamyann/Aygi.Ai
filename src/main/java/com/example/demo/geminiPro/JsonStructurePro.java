package com.example.demo.geminiPro;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Optional;

public class JsonStructurePro {

    @Setter
    @Getter
    public static class GeminiRequest {
        private List<Content> contents;

        public GeminiRequest() {
        }

        public GeminiRequest(List<Content> contents) {
            this.contents = contents;
        }

    }
    @Setter
    @Getter
    public static class Part {
        private String text;

        public Part() {
        }

        public Part(String text) {
            this.text = text;
        }

    }

    @Setter
    @Getter
    public static class TextPart extends Part {
        private String text;

        public TextPart() {
        }

        public TextPart(String text) {
            this.text = text;
        }

    }

    @Setter
    @Getter
    public static class Content {
        private List<Part> parts;

        public Content() {
        }

        public Content(List<Part> parts) {
            this.parts = parts;
        }

    }


    @Setter
    @Getter
    @JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
    public static class InlineDataPart extends Part {
        private InlineData inlineData;

        public InlineDataPart() {
        }

        public InlineDataPart(InlineData inlineData) {
            this.inlineData = inlineData;
        }

    }

    @Setter
    @Getter
    @JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
    public static class InlineData {
        private String mimeType;
        private String data;

        public InlineData() {
        }

        public InlineData(String mimeType, String data) {
            this.mimeType = mimeType;
            this.data = data;
        }

    }

    @Setter
    @Getter
    public static class GeminiResponse {
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private List<Candidate> candidates;
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private PromptFeedback promptFeedback;

        public GeminiResponse() {
        }

        public GeminiResponse(List<Candidate> candidates, PromptFeedback promptFeedback) {
            this.candidates = candidates;
            this.promptFeedback = promptFeedback;
        }

    }

    @Setter
    @Getter
    public static class Candidate {
        private Optional<Content> content;
        private String finishReason;
        private int index;
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private List<SafetyRating> safetyRatings;

        public Candidate() {
        }

        public Candidate(Optional<Content> content, String finishReason, int index, List<SafetyRating> safetyRatings) {
            this.content = content;
            this.finishReason = finishReason;
            this.index = index;
            this.safetyRatings = safetyRatings;
        }

    }

    @Setter
    @Getter
    public static class SafetyRating {
        private String category;
        private String probability;

        public SafetyRating() {
        }

        public SafetyRating(String category, String probability) {
            this.category = category;
            this.probability = probability;
        }

    }

    @Setter
    @Getter
    public static class PromptFeedback {
        private List<SafetyRating> safetyRatings;

        public PromptFeedback() {
        }

        public PromptFeedback(List<SafetyRating> safetyRatings) {
            this.safetyRatings = safetyRatings;
        }

    }

    // Returned from "count" endpoint
    @Setter
    @Getter
    public static class GeminiCountResponse {
        private int totalTokens;

        public GeminiCountResponse() {
        }

        public GeminiCountResponse(int totalTokens) {
            this.totalTokens = totalTokens;
        }

    }

    @Setter
    @Getter
    public static class ModelList {
        private List<Model> models;

        public ModelList() {
        }

        public ModelList(List<Model> models) {
            this.models = models;
        }

    }

    @Setter
    @Getter
    public static class Model {
        private String name;
        private String version;
        private String displayName;
        private String description;
        private int inputTokenLimit;
        private int outputTokenLimit;
        private List<String> supportedGenerationMethods;
        private double temperature;
        private double topP;
        private int topK;

        public Model() {
        }

        public Model(String name, String version, String displayName, String description, int inputTokenLimit, int outputTokenLimit, List<String> supportedGenerationMethods, double temperature, double topP, int topK) {
            this.name = name;
            this.version = version;
            this.displayName = displayName;
            this.description = description;
            this.inputTokenLimit = inputTokenLimit;
            this.outputTokenLimit = outputTokenLimit;
            this.supportedGenerationMethods = supportedGenerationMethods;
            this.temperature = temperature;
            this.topP = topP;
            this.topK = topK;
        }

    }
}
