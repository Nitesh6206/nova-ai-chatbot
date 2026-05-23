package com.nitesh.novaai.chatbot.backend.Repository;

import com.nitesh.novaai.chatbot.backend.Entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findByConversationIdOrderByCreatedAtAsc(Long conversationId);
}