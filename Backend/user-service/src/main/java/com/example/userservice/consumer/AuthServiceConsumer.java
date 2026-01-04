package com.example.userservice.consumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.example.userservice.entities.UserInfoDto;
import com.example.userservice.service.UserService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthServiceConsumer {
    @Autowired
    private UserService userService;


    @KafkaListener(topics = "${app.kafka.topic.user-info-json}", groupId = "${spring.kafka.consumer.group-id}")
    public void listen(UserInfoDto eventData) {
        try {
            System.out.println("AuthServiceConsumer: Received Kafka event - userId: " + (eventData != null ? eventData.getUserId() : "null"));
            if (eventData == null) {
                System.err.println("AuthServiceConsumer: Received null eventData");
                return;
            }
            userService.validateUserInfo(eventData);
            UserInfoDto savedUser = userService.createOrUpdateUser(eventData);
            System.out.println("AuthServiceConsumer: Successfully saved user with userId: " + (savedUser != null ? savedUser.getUserId() : "null"));
        } catch (Exception ex) {
            System.err.println("AuthServiceConsumer: Exception is thrown while consuming kafka event - " + ex.getMessage());
            ex.printStackTrace();
        }
    }
}
