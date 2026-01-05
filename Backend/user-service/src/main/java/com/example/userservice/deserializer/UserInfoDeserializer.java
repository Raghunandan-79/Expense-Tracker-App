package com.example.userservice.deserializer;

import java.util.Map;

import org.apache.kafka.common.serialization.Deserializer;

import com.example.userservice.entities.UserInfoDto;
import com.fasterxml.jackson.databind.ObjectMapper;

public class UserInfoDeserializer implements Deserializer<UserInfoDto> {
    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {}

    @Override
    public UserInfoDto deserialize(String arg0, byte[] arg1) {
        ObjectMapper objectMapper = new ObjectMapper();
        UserInfoDto user = null;

        try {
            if (arg1 == null || arg1.length == 0) {
                System.err.println("UserInfoDeserializer: Received null or empty byte array");
                return null;
            }
            String jsonString = new String(arg1);
            System.out.println("UserInfoDeserializer: Received JSON: " + jsonString);
            user = objectMapper.readValue(arg1, UserInfoDto.class);
            if (user != null) {
                System.out.println("UserInfoDeserializer: Successfully deserialized - userId: " + user.getUserId() + 
                    ", firstName: " + user.getFirstName() + 
                    ", lastName: " + user.getLastName() + 
                    ", email: " + user.getEmail() + 
                    ", phoneNumber: " + user.getPhoneNumber());
            } else {
                System.err.println("UserInfoDeserializer: Deserialized user is null");
            }
        } catch (Exception e) {
            System.err.println("UserInfoDeserializer: Can not deserialize - " + e.getMessage());
            e.printStackTrace();
        }

        return user;
    }

    @Override
    public void close() {}
}
