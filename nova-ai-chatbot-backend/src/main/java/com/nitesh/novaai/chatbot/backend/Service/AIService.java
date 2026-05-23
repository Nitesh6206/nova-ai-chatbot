package com.nitesh.novaai.chatbot.backend.Service;

import com.nitesh.novaai.chatbot.backend.Entity.Message;

import java.util.List;

public interface AIService {

    String getAIResponse(String userMessage);

    // Future mein better context ke liye
    String getAIResponseWithHistory(String userMessage, List<Message> history);
}