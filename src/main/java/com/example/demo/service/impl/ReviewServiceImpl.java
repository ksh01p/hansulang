// src/main/java/com/example/demo/service/impl/ReviewServiceImpl.java
package com.example.demo.service.impl;

import com.example.demo.domain.Menu;
import com.example.demo.domain.Review;
import com.example.demo.domain.User;
import com.example.demo.dto.ReviewRequestDto;
import com.example.demo.repository.MenuRepository;
import com.example.demo.repository.ReviewRepository;
import com.example.demo.service.ReviewService;
import com.example.demo.service.MenuService;     // MenuService 주입
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
    private final MenuService      menuService; // 통계 업데이트용 주입

    @Override
    @Transactional
    public Review createReview(Long menuId,
                               ReviewRequestDto dto,
                               User currentUser,
                               String photoUrl) {
        Menu menu = menuRepo.findById(menuId)
                .orElseThrow(() -> new EntityNotFoundException("메뉴를 찾을 수 없습니다. id=" + menuId));

        Review review = new Review();
        review.setMenu(menu);
        review.setUser(currentUser);
        review.setUserName(currentUser.getUsername());
        review.setScore(dto.getScore());
        review.setContent(dto.getContent());
        review.setRecommend(dto.isRecommend());
        review.setPhotoUrl(photoUrl);
        review.setLikeCount(0);

        Review saved = reviewRepo.save(review);

        // ★ 리뷰 생성 후, 해당 메뉴의 리뷰 통계(개수와 평균)도 갱신
        List<Review> allReviews = reviewRepo.findByMenuId(menuId);
        int   newCount = allReviews.size();
        double newAvg  = allReviews.stream()
                .mapToInt(Review::getScore)
                .average()
                .orElse(0.0);
        newAvg = Math.round(newAvg * 10) / 10.0;
        menuService.updateReviewStats(menuId, newCount, newAvg);

        return saved;
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

        // ★ 리뷰 삭제 후에도 메뉴 통계를 갱신
        List<Review> allReviews = reviewRepo.findByMenuId(menuId);
        int   newCount = allReviews.size();
        double newAvg  = allReviews.stream()
                .mapToInt(Review::getScore)
                .average()
                .orElse(0.0);
        newAvg = Math.round(newAvg * 10) / 10.0;
        menuService.updateReviewStats(menuId, newCount, newAvg);
    }
    @Override
    @Transactional
    public void incrementLikeCount(Long reviewId) {
        Review review = reviewRepo.findById(reviewId)
                .orElseThrow(() -> new EntityNotFoundException("리뷰 없음: " + reviewId));
        review.setLikeCount(review.getLikeCount() + 1);
    }

    private void updateMenuReviewStats(Long menuId) {
        List<Review> allReviews = reviewRepo.findByMenuId(menuId);
        int newCount = allReviews.size();
        double newAvg = allReviews.stream()
                .mapToInt(Review::getScore)
                .average()
                .orElse(0.0);
        newAvg = Math.round(newAvg * 10) / 10.0;
        menuService.updateReviewStats(menuId, newCount, newAvg);
    }
}
