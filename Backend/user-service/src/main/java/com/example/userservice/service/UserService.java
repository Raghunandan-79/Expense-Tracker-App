package com.example.userservice.service;

import java.util.Optional;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.userservice.entities.UserInfo;
import com.example.userservice.entities.UserInfoDto;
import com.example.userservice.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {
    @Autowired
    private final UserRepository userRepository;

    public UserInfoDto createOrUpdateUser(UserInfoDto userInfoDto) {
        UnaryOperator<UserInfo> updatingUser = user -> {
            return userRepository.save(userInfoDto.transformToUserInfo());
        };

        Supplier<UserInfo> createUser = () -> {
            return userRepository.save(userInfoDto.transformToUserInfo());
        };

        UserInfo userInfo = userRepository.findByUserId(userInfoDto.getUserId())
                .map(updatingUser)
                .orElseGet(createUser);

        return UserInfoDto.builder()
            .userId(userInfo.getUserId())
            .firstName(userInfo.getFirstName())
            .lastName(userInfo.getLastName())
            .phoneNumber(userInfo.getPhoneNumber())
            .email(userInfo.getEmail())
            .profilePic(userInfo.getProfilePic())
            .build();

    }

    public UserInfoDto getUser(UserInfoDto userInfoDto) throws Exception{
        Optional<UserInfo> userInfoDtoOpt = userRepository.findByUserId(userInfoDto.getUserId());
        
        if (userInfoDtoOpt.isEmpty()){
            throw new Exception("User not found");
        }
        
        UserInfo userInfo = userInfoDtoOpt.get();
            return UserInfoDto.builder()
            .userId(userInfo.getUserId())
            .firstName(userInfo.getFirstName())
            .lastName(userInfo.getLastName())
            .phoneNumber(userInfo.getPhoneNumber())
            .email(userInfo.getEmail())
            .profilePic(userInfo.getProfilePic())
            .build();
    }

    public void validateUserInfo(UserInfoDto dto) {
        if (dto == null) {
            throw new IllegalArgumentException("UserInfoDto is null");
        }
        if (dto.getUserId() == null || dto.getUserId().isBlank()) {
            throw new IllegalArgumentException("user_id is required");
        }
        // Email and phoneNumber are optional - can be updated later
        if (dto.getEmail() != null && !dto.getEmail().contains("@")) {
            throw new IllegalArgumentException("Invalid email");
        }
    }


}
