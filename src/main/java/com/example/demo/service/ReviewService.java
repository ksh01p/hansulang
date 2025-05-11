// src/main/java/com/example/demo/service/ReviewService.java
package com.example.demo.service;

import com.example.demo.domain.Review;
import com.example.demo.domain.User;
import com.example.demo.dto.ReviewRequestDto;

import java.nio.file.AccessDeniedException;
import java.util.List;

public interface ReviewService {
    Review createReview(Long menuId,
                        ReviewRequestDto dto,
                        User currentUser,
                        String photoUrl);
    List<Review> getReviewsByMenuId(Long menuId);
    int countRecommend(Long menuId);
    int countNotRecommend(Long menuId);
    void deleteReview(Long menuId,
                      Long reviewId,
                      User currentUser) throws AccessDeniedException;
}
