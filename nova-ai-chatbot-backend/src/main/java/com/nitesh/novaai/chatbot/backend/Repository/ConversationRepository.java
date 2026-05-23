package com.nitesh.novaai.chatbot.backend.Repository;

import com.nitesh.novaai.chatbot.backend.Entity.Conversation;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ConversationRepository extends JpaRepository<Conversation, Long> {
    List<Conversation> findByUserEmailOrderByUpdatedAtDesc(String email);
}