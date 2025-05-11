// src/main/java/com/example/demo/repository/ReviewRepository.java
package com.example.demo.repository;

import com.example.demo.domain.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByMenuId(Long menuId);
    // 추천/비추천 개수 집계
    int countByMenuIdAndRecommendTrue(Long menuId);
    int countByMenuIdAndRecommendFalse(Long menuId);
}
