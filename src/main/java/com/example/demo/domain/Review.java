// src/main/java/com/example/demo/domain/Review.java
package com.example.demo.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "reviews")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Review {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne @JoinColumn(name = "menu_id", nullable = false)
    private Menu menu;

    @Column(name = "user_name", nullable = false)
    private String userName;       // 작성자 이름

    private int score;             // 1–5
    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    private boolean recommend;     // true=추천, false=비추천

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}
