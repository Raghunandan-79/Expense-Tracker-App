package com.example.userservice.service;

import java.util.Optional;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.userservice.entities.UserInfo;
import com.example.userservice.entities.UserInfoDto;
import com.example.userservice.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    @Autowired
    private final UserRepository userRepository;

    // Email validation pattern
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );
    
    // Phone number validation pattern (10-15 digits, optionally with country code)
    private static final Pattern PHONE_PATTERN = Pattern.compile(
        "^[+]?[0-9]{10,15}$"
    );

    @Transactional
    public UserInfoDto createOrUpdateUser(UserInfoDto userInfoDto) {
        // Idempotency check: If user exists, update only if new data is provided
        Optional<UserInfo> existingUserOpt = userRepository.findByUserId(userInfoDto.getUserId());
        
        UserInfo userInfo;
        if (existingUserOpt.isPresent()) {
            // User exists - update only non-null fields (idempotent update)
            UserInfo existingUser = existingUserOpt.get();
            UserInfo updatedUser = updateUserFields(existingUser, userInfoDto);
            userInfo = userRepository.save(updatedUser);
            log.info("Updated existing user with userId: {}", userInfo.getUserId());
        } else {
            // New user - create
            userInfo = userRepository.save(userInfoDto.transformToUserInfo());
            log.info("Created new user with userId: {}", userInfo.getUserId());
        }

        return UserInfoDto.builder()
            .userId(userInfo.getUserId())
            .firstName(userInfo.getFirstName())
            .lastName(userInfo.getLastName())
            .phoneNumber(userInfo.getPhoneNumber())
            .email(userInfo.getEmail())
            .profilePic(userInfo.getProfilePic())
            .build();
    }
    
    private UserInfo updateUserFields(UserInfo existingUser, UserInfoDto newData) {
        // Only update fields that are provided (non-null) in the new data
        if (newData.getFirstName() != null) {
            existingUser.setFirstName(newData.getFirstName());
        }
        if (newData.getLastName() != null) {
            existingUser.setLastName(newData.getLastName());
        }
        if (newData.getEmail() != null) {
            existingUser.setEmail(newData.getEmail());
        }
        if (newData.getPhoneNumber() != null) {
            existingUser.setPhoneNumber(newData.getPhoneNumber());
        }
        if (newData.getProfilePic() != null) {
            existingUser.setProfilePic(newData.getProfilePic());
        }
        return existingUser;
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
            throw new IllegalArgumentException("UserInfoDto cannot be null");
        }
        
        // userId is required
        if (dto.getUserId() == null || dto.getUserId().isBlank()) {
            throw new IllegalArgumentException("user_id is required and cannot be blank");
        }
        
        // Validate UUID format for userId
        if (!isValidUUID(dto.getUserId())) {
            throw new IllegalArgumentException("user_id must be a valid UUID format");
        }
        
        // Validate email format if provided
        if (dto.getEmail() != null && !dto.getEmail().isBlank()) {
            if (!EMAIL_PATTERN.matcher(dto.getEmail().trim()).matches()) {
                throw new IllegalArgumentException("Invalid email format: " + dto.getEmail());
            }
        }
        
        // Validate phone number format if provided
        if (dto.getPhoneNumber() != null) {
            String phoneStr = String.valueOf(dto.getPhoneNumber());
            if (!PHONE_PATTERN.matcher(phoneStr).matches()) {
                throw new IllegalArgumentException("Invalid phone number format. Must be 10-15 digits: " + phoneStr);
            }
        }
        
        // Validate firstName and lastName if provided (not blank)
        if (dto.getFirstName() != null && dto.getFirstName().isBlank()) {
            throw new IllegalArgumentException("first_name cannot be blank if provided");
        }
        
        if (dto.getLastName() != null && dto.getLastName().isBlank()) {
            throw new IllegalArgumentException("last_name cannot be blank if provided");
        }
        
        log.debug("Validation passed for userId: {}", dto.getUserId());
    }
    
    private boolean isValidUUID(String uuid) {
        try {
            java.util.UUID.fromString(uuid);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }


}
