package com.nitesh.novaai.chatbot.backend.Controller;

import com.nitesh.novaai.chatbot.backend.Entity.Conversation;
import com.nitesh.novaai.chatbot.backend.Entity.Message;
import com.nitesh.novaai.chatbot.backend.Service.AIService;
import com.nitesh.novaai.chatbot.backend.Service.ChatService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/ai")
public class ChatController {

    private final ChatService chatService;
    private final AIService aiService;   // Tumhara existing AI service (Grok ya OpenAI)

    public ChatController(ChatService chatService, AIService aiService) {
        this.chatService = chatService;
        this.aiService = aiService;
    }
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("OK");
    }

    @GetMapping("/chat")
    public ResponseEntity<String> chat(
            @RequestParam String message,
            @RequestParam(required = false) Long conversationId,
            Authentication authentication) {

        String email = authentication.getName();

        if (conversationId == null) {
            Conversation newConv = chatService.getOrCreateConversation(email, null);
            conversationId = newConv.getId();
        }

        // Save User Message
        chatService.saveUserMessage(conversationId, message);

        // Get AI Response
        String aiResponse = aiService.getAIResponse(message);

        // Save AI Message
        chatService.saveAIMessage(conversationId, aiResponse);

        return ResponseEntity.ok(aiResponse);
    }


    // Get All User Chats (Sidebar ke liye)
    @GetMapping("/conversations")
    public ResponseEntity<List<Conversation>> getConversations(Authentication authentication) {
        String email = authentication.getName();
        return ResponseEntity.ok(chatService.getUserConversations(email));
    }

    // Get Messages of a Conversation
    @GetMapping("/conversations/{conversationId}/messages")
    public ResponseEntity<List<Message>> getMessages(@PathVariable Long conversationId) {
        return ResponseEntity.ok(chatService.getConversationHistory(conversationId));
    }
}