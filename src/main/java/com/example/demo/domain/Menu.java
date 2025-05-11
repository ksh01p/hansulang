// src/main/java/com/example/demo/domain/Menu.java
package com.example.demo.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "menu_items")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Menu {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String restaurant;
    private String name;
    private Double price;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "image_url")
    private String imageUrl;

    /** 작성자 **/
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User createdBy;

    /** 통계 필드 **/
    @Column(name = "recommend_count", nullable = false)
    private Integer recommendCount = 0;

    @Column(name = "not_recommend_count", nullable = false)
    private Integer notRecommendCount = 0;
}
