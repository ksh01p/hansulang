// src/main/java/com/example/demo/dto/MenuDto.java
package com.example.demo.dto;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class MenuDto {
    private Long id;
    private String restaurant;
    private String name;
    private Double price;
    private String description;
    private String imageUrl;
    private Long createdById;
    private String createdByUsername;
    private int recommendCount;
    private int notRecommendCount;
}
