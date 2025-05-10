package com.example.demo.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReviewRequestDto {
    private String userName;
    private int score;
    private String title;
    private String content;
    private boolean recommend;

}
