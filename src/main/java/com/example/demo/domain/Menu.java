// src/main/java/com/example/demo/domain/Menu.java
package com.example.demo.domain;

import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;

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

    /** 기존 통계 필드 **/
    @Column(name = "recommend_count", nullable = false)
    private Integer recommendCount = 0;

    @Column(name = "not_recommend_count", nullable = false)
    private Integer notRecommendCount = 0;

    /** 여기에 리뷰 통계 컬럼 추가 **/
    @Column(name = "review_count", nullable = false)
    private Integer reviewCount = 0;

    @Column(name = "avg_score", nullable = false)
    private Double avgScore = 0.0;

    /** 학관 메뉴 여부 **/
    @Column(name = "hisnet", nullable = true)
    private Integer hisnet = 0;

    @Column(name = "created_at", nullable = true)
    private Timestamp created_at;

    @Column(name = "updated_at", nullable = true)
    private Timestamp updated_at;
}
