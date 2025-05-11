// src/main/java/com/example/demo/dto/ReviewDto.java
package com.example.demo.dto;

import lombok.*;
import java.time.LocalDateTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class ReviewDto {
    private Long id;
    private Long menuId;
    private Long userId;
    private String userName;
    private int score;
    private String content;
    private boolean recommend;
    private String photoUrl;
    private int likeCount;
    private LocalDateTime createdAt;
}
