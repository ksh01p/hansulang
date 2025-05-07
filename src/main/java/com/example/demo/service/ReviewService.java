// src/main/java/com/example/demo/service/ReviewService.java
package com.example.demo.service;

import com.example.demo.domain.Review;
import java.util.List;

public interface ReviewService {
    Review createReview(Long menuId, Review review);
    List<Review> getReviewsByMenuId(Long menuId);
}
