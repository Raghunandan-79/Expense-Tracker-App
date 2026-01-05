package com.example.authservice.serializer;

import java.util.Map;

import org.apache.kafka.common.serialization.Serializer;

import com.example.authservice.model.UserInfoDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;

public class UserInfoSerializer implements Serializer<UserInfoDto> {
    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {}

    @Override
    public byte[] serialize(String s, UserInfoDto userInfoDto) {
        byte[] retVal = null;
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
        
        try {
            if (userInfoDto != null) {
                String json = objectMapper.writeValueAsString(userInfoDto);
                System.out.println("UserInfoSerializer: Serializing user - userId: " + userInfoDto.getUserId() + 
                    ", firstName: " + userInfoDto.getFirstName() + 
                    ", lastName: " + userInfoDto.getLastName() + 
                    ", email: " + userInfoDto.getEmail() + 
                    ", phoneNumber: " + userInfoDto.getPhoneNumber());
                System.out.println("UserInfoSerializer: JSON: " + json);
                retVal = json.getBytes();
            } else {
                System.err.println("UserInfoSerializer: userInfoDto is null");
            }
        } catch (Exception ex) {
            System.err.println("UserInfoSerializer: Error serializing - " + ex.getMessage());
            ex.printStackTrace();
        }

        return retVal;
    }

    @Override
    public void close() {}
}
