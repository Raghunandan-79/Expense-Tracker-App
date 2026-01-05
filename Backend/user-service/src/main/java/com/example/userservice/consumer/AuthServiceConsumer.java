package com.example.userservice.consumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.userservice.entities.UserInfoDto;
import com.example.userservice.service.UserService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceConsumer {
    @Autowired
    private UserService userService;

    @Value("${app.kafka.topic.user-info-json}")
    private String topicName;

    @Value("${spring.kafka.consumer.group-id}")
    private String groupId;

    @KafkaListener(
        topics = "${app.kafka.topic.user-info-json}", 
        groupId = "${spring.kafka.consumer.group-id}"
    )
    @Transactional
    public void listen(
            @Payload UserInfoDto eventData,
            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset) {
        
        String userId = eventData != null ? eventData.getUserId() : "null";
        log.info("AuthServiceConsumer: Received Kafka event - topic: {}, partition: {}, offset: {}, userId: {}", 
            topic, partition, offset, userId);
        
        try {
            if (eventData == null) {
                log.error("AuthServiceConsumer: Received null eventData - skipping");
                return;
            }
            
            // Validate the incoming data
            userService.validateUserInfo(eventData);
            log.debug("AuthServiceConsumer: Validation passed for userId: {}", eventData.getUserId());
            
            // Create or update user (idempotent operation)
            UserInfoDto savedUser = userService.createOrUpdateUser(eventData);
            
            if (savedUser != null) {
                log.info("AuthServiceConsumer: Successfully processed user with userId: {}", savedUser.getUserId());
            } else {
                log.warn("AuthServiceConsumer: UserService returned null for userId: {}", eventData.getUserId());
            }
            
        } catch (IllegalArgumentException ex) {
            // Validation errors - log but don't retry
            log.error("AuthServiceConsumer: Validation failed for userId: {} - {}", userId, ex.getMessage());
            // Don't throw exception to avoid infinite retries for invalid data
        } catch (Exception ex) {
            // Other errors - log and rethrow to trigger retry mechanism
            log.error("AuthServiceConsumer: Exception processing Kafka event for userId: {} - {}", 
                userId, ex.getMessage(), ex);
            throw ex; // Re-throw to trigger Kafka retry mechanism
        }
    }
}
