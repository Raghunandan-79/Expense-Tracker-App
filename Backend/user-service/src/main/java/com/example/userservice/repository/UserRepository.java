package com.example.userservice.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.example.userservice.entities.UserInfoDto;

@Repository
public interface UserRepository extends CrudRepository<UserInfoDto, String> {
    UserInfoDto findByUserId(String userId);
}
