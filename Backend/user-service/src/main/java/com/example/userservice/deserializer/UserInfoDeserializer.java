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
            user = objectMapper.readValue(arg1, UserInfoDto.class);
        } catch (Exception e) {
            System.err.println("Can not deserialize");
        }

        return user;
    }

    @Override
    public void close() {}
}
