package com.nitesh.novaai.chatbot.backend.Service;

import com.nitesh.novaai.chatbot.backend.Entity.User;
import com.nitesh.novaai.chatbot.backend.Repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User getOrCreateUser(String email, String name, String picture) {
        return userRepository.findByEmail(email)
                .orElseGet(() -> {
                    User user = new User();
                    user.setEmail(email);
                    user.setName(name != null ? name : email.split("@")[0]);
                    user.setPicture(picture);
                    return userRepository.save(user);
                });
    }
}