package com.example.demo.dto;

import lombok.*;
import java.time.LocalDateTime;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReviewDto {
    private Long id;
    private Long menuId;
    private String userName;
    private String content;
    private LocalDateTime createdAt;
}
