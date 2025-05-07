// src/main/java/com/example/demo/service/impl/ReviewServiceImpl.java
package com.example.demo.service.impl;

import com.example.demo.domain.Menu;
import com.example.demo.domain.Review;
import com.example.demo.repository.MenuRepository;
import com.example.demo.repository.ReviewRepository;
import com.example.demo.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {
    private final ReviewRepository reviewRepo;
    private final MenuRepository menuRepo;

    @Override
    public Review createReview(Long menuId, Review review) {
        Menu menu = menuRepo.findById(menuId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid menu id: " + menuId));
        review.setMenu(menu);
        return reviewRepo.save(review);
    }


    @Override
    public List<Review> getReviewsByMenuId(Long menuId) {
        return reviewRepo.findByMenuId(menuId);
    }
}
