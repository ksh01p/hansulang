// src/main/java/com/example/demo/service/ReviewService.java
package com.example.demo.service;

import com.example.demo.domain.Review;
import com.example.demo.dto.ReviewRequestDto;

import java.util.List;

public interface ReviewService {
    Review createReview(Long menuId, ReviewRequestDto dto);
    List<Review> getReviewsByMenuId(Long menuId);
}
