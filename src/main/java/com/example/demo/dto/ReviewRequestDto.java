// src/main/java/com/example/demo/dto/ReviewRequestDto.java
package com.example.demo.dto;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class ReviewRequestDto {
    private int score;
    private String content;
    private boolean recommend;


}
