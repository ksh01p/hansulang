// src/main/java/com/example/demo/service/impl/ReviewServiceImpl.java
package com.example.demo.service.impl;

import com.example.demo.domain.Menu;
import com.example.demo.domain.Review;
import com.example.demo.domain.User;
import com.example.demo.dto.ReviewRequestDto;
import com.example.demo.repository.MenuRepository;
import com.example.demo.repository.ReviewRepository;
import com.example.demo.service.ReviewService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.AccessDeniedException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepo;
    private final MenuRepository   menuRepo;

    @Override
    @Transactional
    public Review createReview(Long menuId,
                               ReviewRequestDto dto,
                               User currentUser,
                               String photoUrl) {
        Menu menu = menuRepo.findById(menuId)
                .orElseThrow(() -> new EntityNotFoundException("메뉴를 찾을 수 없습니다. id=" + menuId));

        if (currentUser.getUsername() == null || currentUser.getUsername().trim().isEmpty()) {
            throw new IllegalArgumentException("작성자 이름이 없습니다.");
        }

        if (dto.getContent() == null || dto.getContent().trim().isEmpty()) {
            throw new IllegalArgumentException("리뷰 내용은 비어있을 수 없습니다.");
        }

        Review review = new Review();
        review.setMenu(menu);
        review.setUser(currentUser);
        review.setUserName(currentUser.getUsername());
        review.setScore(dto.getScore());
        review.setContent(dto.getContent());
        review.setRecommend(dto.isRecommend());
        review.setPhotoUrl(photoUrl);
        review.setLikeCount(0);

        return reviewRepo.save(review);
    }

    @Override
    public List<Review> getReviewsByMenuId(Long menuId) {
        return reviewRepo.findByMenuId(menuId);
    }

    @Override
    public int countRecommend(Long menuId) {
        return reviewRepo.countByMenuIdAndRecommendTrue(menuId);
    }

    @Override
    public int countNotRecommend(Long menuId) {
        return reviewRepo.countByMenuIdAndRecommendFalse(menuId);
    }

    @Override
    @Transactional
    public void deleteReview(Long menuId,
                             Long reviewId,
                             User currentUser) throws AccessDeniedException {
        Review review = reviewRepo.findById(reviewId)
                .orElseThrow(() -> new EntityNotFoundException("리뷰 없음: " + reviewId));
        if (!review.getUser().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("본인의 리뷰만 삭제할 수 있습니다.");
        }
        reviewRepo.delete(review);
    }
}
