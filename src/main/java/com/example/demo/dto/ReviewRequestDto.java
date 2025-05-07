package com.example.demo.dto;

import lombok.*;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReviewRequestDto {
    private Long menuId;
    private String userName;
    private String content;
}
