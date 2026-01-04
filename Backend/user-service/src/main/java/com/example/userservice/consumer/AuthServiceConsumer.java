package com.example.userservice.consumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;

import com.example.userservice.repository.UserRepository;

public class AuthServiceConsumer {
    private UserRepository userRepository;

    @Autowired
    AuthServiceConsumer(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @KafkaListener(topics = "${app.kafka.topic.user-info-json}", groupId = "${spring.kafka.consumer.group-id}")
    public void listen(Object eventDate) {
        try {
            
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
