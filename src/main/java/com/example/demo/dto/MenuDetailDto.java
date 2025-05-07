// src/main/java/com/example/demo/dto/MenuDetailDto.java
package com.example.demo.dto;

import lombok.*;

import java.util.List;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class MenuDetailDto {
    private String title;       // ex: "든든한동 (아침)"
    private List<String> items; // ex: ["쌀밥","어묵국",…]
}
