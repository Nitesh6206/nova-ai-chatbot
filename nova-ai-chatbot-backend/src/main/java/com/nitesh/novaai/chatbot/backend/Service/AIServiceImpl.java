package com.nitesh.novaai.chatbot.backend.Service;

import com.nitesh.novaai.chatbot.backend.Entity.Message;
import com.nitesh.novaai.chatbot.backend.Service.AIService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
public class AIServiceImpl implements AIService {

    private final RestTemplate restTemplate;

    @Value("${spring.ai.google.genai.api-key}")
    private String geminiApiKey;

    @Value("${spring.ai.google.genai.model:gemini-2.0-flash-exp}")
    private String modelName;

    @Value("${spring.ai.google.genai.temperature:0.7}")
    private double temperature;

    @Value("${spring.ai.google.genai.max-output-tokens:800}")
    private int maxOutputTokens;

    private final String BASE_URL = "https://generativelanguage.googleapis.com/v1beta/models/";

    public AIServiceImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public String getAIResponse(String userMessage) {
        try {
            String url = BASE_URL + modelName + ":generateContent?key=" + geminiApiKey;

            System.out.println("🔍 Calling Gemini URL: " + url);  // Debug

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            Map<String, Object> requestBody = Map.of(
                    "contents", List.of(Map.of("parts", List.of(Map.of("text", userMessage)))),
                    "generationConfig", Map.of(
                            "temperature", temperature,
                            "maxOutputTokens", maxOutputTokens
                    )
            );

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            Map<String, Object> response = restTemplate.postForObject(url, entity, Map.class);

            System.out.println("✅ Gemini Response Received"); // Debug

            if (response != null && response.containsKey("candidates")) {
                List<Map<String, Object>> candidates = (List<Map<String, Object>>) response.get("candidates");
                if (!candidates.isEmpty()) {
                    Map<String, Object> content = (Map<String, Object>) candidates.get(0).get("content");
                    List<Map<String, Object>> parts = (List<Map<String, Object>>) content.get("parts");
                    if (!parts.isEmpty() && parts.get(0).get("text") != null) {
                        return (String) parts.get(0).get("text");
                    }
                }
            }

            return "Sorry, AI se response nahi mil raha hai.";

        } catch (Exception e) {
            System.err.println("❌ Gemini API Error: " + e.getMessage());
            e.printStackTrace();
            return "Sorry, main abhi thoda busy hoon. Baad mein try karna.";
        }
    }

    @Override
    public String getAIResponseWithHistory(String userMessage, List<Message> history) {
        StringBuilder fullPrompt = new StringBuilder();
        for (Message msg : history) {
            fullPrompt.append(msg.getSender().equals("user") ? "User: " : "AI: ")
                    .append(msg.getContent())
                    .append("\n");
        }
        fullPrompt.append("User: ").append(userMessage);

        return getAIResponse(fullPrompt.toString());
    }
}