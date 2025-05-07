// src/main/java/com/example/demo/domain/Menu.java
package com.example.demo.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "menu_items")      // ← 여기를 menus → menu_items 로 변경
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Menu {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private Double price;

    @Column(columnDefinition = "TEXT")
    private String description;
}
