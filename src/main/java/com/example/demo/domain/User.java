package com.example.demo.domain;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@Entity

public class User {
    @Id
    @GeneratedValue
    private Long id;//User 고유 PK
    private String username; // User ID
    private String password; // User PW
    private String role; // 권한(admin 혹은 user) --> 시큐리티 구현 X
}

