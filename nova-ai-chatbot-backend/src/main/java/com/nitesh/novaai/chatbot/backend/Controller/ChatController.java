package com.nitesh.novaai.chatbot.backend.Controller;

import org.springframework.ai.chat.model.ChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ChatController {

    private final ChatModel chatModel;

    @Autowired
    public ChatController(ChatModel chatModel) {
        this.chatModel = chatModel;
    }

    @GetMapping("/ai/chat")
    public String generate(
            @RequestParam(value = "message", defaultValue = "Tell me a joke") String message) {

        return chatModel.call(message);
    }
}