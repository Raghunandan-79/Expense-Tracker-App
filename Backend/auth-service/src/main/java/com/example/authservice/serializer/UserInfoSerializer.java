package com.example.authservice.serializer;

import java.util.Map;

import org.apache.kafka.common.serialization.Serializer;

import com.example.authservice.model.UserInfoDto;
import com.fasterxml.jackson.databind.ObjectMapper;

public class UserInfoSerializer implements Serializer<UserInfoDto> {
    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {}

    @Override
    public byte[] serialize(String s, UserInfoDto userInfoDto) {
        byte[] retVal = null;
        ObjectMapper objectMapper = new ObjectMapper();
        
        try {
            retVal = objectMapper.writeValueAsString(userInfoDto).getBytes();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return retVal;
    }

    @Override
    public void close() {}
}
