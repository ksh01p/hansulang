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

    /** 작성자 **/
    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /** 대상 메뉴 **/
    @ManyToOne
    @JoinColumn(name = "menu_id", nullable = false)
    private Menu menu;

    /** 사용자가 남긴 이름(후보 필드) **/
    @Column(name = "user_name", nullable = false)
    private String userName;

    private int score;

    @Column(columnDefinition = "TEXT")
    private String content;

    private boolean recommend;

    @Column(name = "photo_url")
    private String photoUrl;

    @Column(name = "like_count", nullable = false)
    private Integer likeCount = 0;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}
