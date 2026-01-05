package com.example.authservice.service;


import java.util.HashSet;
import java.util.Objects;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.example.authservice.entities.UserInfo;
import com.example.authservice.eventProducer.UserInfoProducer;
import com.example.authservice.model.UserInfoDto;
import com.example.authservice.repository.UserRepository;

import lombok.AllArgsConstructor;
import lombok.Data;

@Component
@AllArgsConstructor
@Data
public class UserDetailsServiceImpl implements UserDetailsService {
    @Autowired
    private final UserRepository userRepository;

    @Autowired
    private final PasswordEncoder passwordEncoder;

    @Autowired
    private final UserInfoProducer userInfoProducer;

    private static final Logger log = LoggerFactory.getLogger(UserDetailsServiceImpl.class);

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.debug("Entering in loadUserByUsername Method...");
        UserInfo user = userRepository.findByUsername(username);

        if (user == null) {
            log.error("Username not found: " + username);
            throw new UsernameNotFoundException("Could not find user..!!");
        }

        log.info("User Authenticated Successfully...");

        return new CustomUserDetails(user);
    }

    public UserInfo checkIfUserAlreadyExists(UserInfoDto userInfoDto) {
        return userRepository.findByUsername(userInfoDto.getUsername());
    }

    public Boolean signupUser(UserInfoDto userInfoDto) {
        userInfoDto.setPassword(passwordEncoder.encode(userInfoDto.getPassword()));
        
        if (Objects.nonNull(checkIfUserAlreadyExists(userInfoDto))) {
            return false;
        }

        String userId = UUID.randomUUID().toString();
        userInfoDto.setUserId(userId);
        userRepository.save(new UserInfo(userId, userInfoDto.getUsername(), userInfoDto.getPassword(), new HashSet<>()));

        log.info("Sending user to Kafka - userId: {}, username: {}, firstName: {}, lastName: {}, email: {}, phoneNumber: {}", 
            userId, userInfoDto.getUsername(), userInfoDto.getFirstName(), userInfoDto.getLastName(), 
            userInfoDto.getEmail(), userInfoDto.getPhoneNumber());
        userInfoProducer.sendEventToKafka(userInfoDto);

        return true;
    }
}