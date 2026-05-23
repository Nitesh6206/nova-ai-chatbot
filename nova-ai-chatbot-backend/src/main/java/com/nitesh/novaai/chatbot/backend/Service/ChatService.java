package com.nitesh.novaai.chatbot.backend.Service;

import com.nitesh.novaai.chatbot.backend.Entity.Conversation;
import com.nitesh.novaai.chatbot.backend.Entity.Message;
import com.nitesh.novaai.chatbot.backend.Entity.User;
import com.nitesh.novaai.chatbot.backend.Repository.ConversationRepository;
import com.nitesh.novaai.chatbot.backend.Repository.MessageRepository;
import com.nitesh.novaai.chatbot.backend.Repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ChatService {

    private final UserRepository userRepository;
    private final ConversationRepository conversationRepository;
    private final MessageRepository messageRepository;

    public ChatService(UserRepository userRepository,
                       ConversationRepository conversationRepository,
                       MessageRepository messageRepository) {
        this.userRepository = userRepository;
        this.conversationRepository = conversationRepository;
        this.messageRepository = messageRepository;
    }

    // Get or Create Conversation
    public Conversation getOrCreateConversation(String email, String title) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Agar title nahi diya toh default banao
        if (title == null || title.trim().isEmpty()) {
            title = "Chat " + System.currentTimeMillis();
        }

        Conversation conversation = new Conversation();
        conversation.setUser(user);
        conversation.setTitle(title);
        return conversationRepository.save(conversation);
    }

    // Save User Message
    public Message saveUserMessage(Long conversationId, String content) {
        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new RuntimeException("Conversation not found"));

        Message message = new Message();
        message.setConversation(conversation);
        message.setSender("user");
        message.setContent(content);

        return messageRepository.save(message);
    }

    // Save AI Message
    public Message saveAIMessage(Long conversationId, String content) {
        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new RuntimeException("Conversation not found"));

        Message message = new Message();
        message.setConversation(conversation);
        message.setSender("ai");
        message.setContent(content);

        return messageRepository.save(message);
    }

    // Get Conversation History
    public List<Message> getConversationHistory(Long conversationId) {
        return messageRepository.findByConversationIdOrderByCreatedAtAsc(conversationId);
    }

    // Get All User Conversations
    public List<Conversation> getUserConversations(String email) {
        return conversationRepository.findByUserEmailOrderByUpdatedAtDesc(email);
    }

}