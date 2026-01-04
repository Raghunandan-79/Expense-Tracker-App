package com.example.userservice.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(name = "users")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class UserInfo {

    @Id
    private String userId;
    
    private String firstName;
    private String lastName;
    private Long phoneNumber;
    private String email;
    private String profilePic;
}
