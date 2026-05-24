package com.nitesh.novaai.chatbot.backend.Service;

import com.nitesh.novaai.chatbot.backend.Entity.Message;
import com.nitesh.novaai.chatbot.backend.Service.AIService;
import com.nitesh.novaai.chatbot.backend.Service.GoogleCalendarService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class AIServiceImpl implements AIService {

    private final RestTemplate restTemplate;
    private final GoogleCalendarService calendarService;

    @Value("${spring.ai.google.genai.api-key}")
    private String geminiApiKey;

    @Value("${spring.ai.google.genai.model:gemini-1.5-flash-latest}")
    private String modelName;

    public AIServiceImpl(RestTemplate restTemplate, GoogleCalendarService calendarService) {
        this.restTemplate = restTemplate;
        this.calendarService = calendarService;
    }

    @Override
    public String getAIResponse(String userMessage) {
        String lower = userMessage.toLowerCase().trim();

        // Calendar Scheduling Intent Detection
        if (lower.contains("schedule") || lower.contains("meeting") ||
                lower.contains("appointment") || lower.contains("book") ||
                lower.contains("set reminder")) {

            return handleCalendarScheduling(userMessage);
        }

        // Normal Chat Response
        return callGeminiAPI(userMessage);
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

    // ==================== Calendar Scheduling ====================
    private String handleCalendarScheduling(String userMessage) {
        try {
            String prompt = """
                    Extract event details from this message and return ONLY valid JSON.
                    Message: %s
                    
                    Format:
                    {
                      "title": "Meeting with John",
                      "startTime": "2026-05-25T15:30:00",
                      "description": "Discuss project updates"
                    }
                    """.formatted(userMessage);

            String aiJson = callGeminiAPI(prompt);

            // Extract values using regex
            String title = extractValue(aiJson, "title");
            String startTimeStr = extractValue(aiJson, "startTime");
            String description = extractValue(aiJson, "description");

            if (title.isEmpty() || startTimeStr.isEmpty()) {
                return "I couldn't understand the event details properly. Please tell me clearly like: 'Schedule meeting with Rahul tomorrow at 4 PM'";
            }

            LocalDateTime startTime = LocalDateTime.parse(startTimeStr);

            // Create event in Google Calendar
            String result = calendarService.createEvent(
                    "user@example.com", // Replace with actual user email from auth
                    title,
                    startTime,
                    description
            );

            return result;

        } catch (Exception e) {
            e.printStackTrace();
            return "Sorry, I couldn't schedule the event. Please try again with clear date and time.";
        }
    }

    // ==================== Gemini API Call ====================
    private String callGeminiAPI(String prompt) {
        try {
            String url = "https://generativelanguage.googleapis.com/v1beta/models/"
                    + modelName + ":generateContent?key=" + geminiApiKey;

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            Map<String, Object> requestBody = Map.of(
                    "contents", List.of(
                            Map.of("parts", List.of(Map.of("text", prompt)))
                    ),
                    "generationConfig", Map.of(
                            "temperature", 0.7,
                            "maxOutputTokens", 800
                    )
            );

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            Map<String, Object> response = restTemplate.postForObject(url, entity, Map.class);

            if (response != null && response.containsKey("candidates")) {
                List<Map<String, Object>> candidates = (List<Map<String, Object>>) response.get("candidates");
                if (!candidates.isEmpty()) {
                    Map<String, Object> content = (Map<String, Object>) candidates.get(0).get("content");
                    List<Map<String, Object>> parts = (List<Map<String, Object>>) content.get("parts");
                    if (!parts.isEmpty()) {
                        return (String) parts.get(0).get("text");
                    }
                }
            }

            return "{}"; // fallback empty JSON

        } catch (Exception e) {
            e.printStackTrace();
            return "{}";
        }
    }

    // ==================== Helper Method ====================
    private String extractValue(String json, String key) {
        if (json == null) return "";
        Pattern pattern = Pattern.compile("\"" + key + "\":\\s*\"(.*?)\"");
        Matcher matcher = pattern.matcher(json);
        return matcher.find() ? matcher.group(1) : "";
    }
}