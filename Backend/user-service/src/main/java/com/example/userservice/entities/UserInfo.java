package com.example.userservice.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(name = "users", 
       uniqueConstraints = {
           @jakarta.persistence.UniqueConstraint(name = "uk_email", columnNames = "email"),
           @jakarta.persistence.UniqueConstraint(name = "uk_phone_number", columnNames = "phone_number")
       })
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class UserInfo {

    @Id
    @Column(name = "user_id", nullable = false, unique = true)
    private String userId;
    
    @Column(name = "first_name")
    private String firstName;
    
    @Column(name = "last_name")
    private String lastName;
    
    @Column(name = "phone_number", unique = true)
    private Long phoneNumber;
    
    @Column(name = "email", unique = true)
    private String email;
    
    @Column(name = "profile_pic")
    private String profilePic;
}
