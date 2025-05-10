package com.example.demo.service.impl;

import com.example.demo.domain.Menu;
import com.example.demo.domain.Review;
import com.example.demo.dto.ReviewRequestDto;
import com.example.demo.repository.MenuRepository;
import com.example.demo.repository.ReviewRepository;
import com.example.demo.service.ReviewService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepo;
    private final MenuRepository menuRepo;

    @Override
    @Transactional
    public Review createReview(Long menuId, ReviewRequestDto dto) {
        Menu menu = menuRepo.findById(menuId)
                .orElseThrow(() -> new EntityNotFoundException("해당 메뉴를 찾을 수 없습니다. id=" + menuId));

        Review review = new Review();
        review.setMenu(menu);
        review.setUserName(dto.getUserName());
        review.setScore(dto.getScore());
        review.setTitle(dto.getTitle());
        review.setContent(dto.getContent());
        review.setRecommend(dto.isRecommend());

        return reviewRepo.save(review);
    }

    @Override
    public List<Review> getReviewsByMenuId(Long menuId) {
        return reviewRepo.findByMenuId(menuId);
    }
}
