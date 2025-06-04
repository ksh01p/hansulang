// src/main/java/com/example/demo/controller/MenuRestController.java
package com.example.demo.controller;

import com.example.demo.domain.Menu;
import com.example.demo.domain.Review;
import com.example.demo.domain.User;
import com.example.demo.dto.MenuDto;
import com.example.demo.dto.ReviewDto;
import com.example.demo.dto.ReviewRequestDto;
import com.example.demo.service.FileService;
import com.example.demo.service.MenuService;
import com.example.demo.service.ReviewService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.AccessDeniedException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/menus")
@RequiredArgsConstructor
public class MenuRestController {

    private final MenuService    menuService;
    private final ReviewService  reviewService;
    private final FileService    fileService;

    /** (1) 메뉴 등록 (JPA 사용) **/
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<MenuDto> createMenu(
            HttpSession session,
            @RequestParam String restaurant,
            @RequestParam String name,
            @RequestParam Double price,
            @RequestParam String description,
            @RequestParam(value = "file", required = false) MultipartFile file
    ) {
        User currentUser = (User) session.getAttribute("loginUser");
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String imageUrl = (file != null && !file.isEmpty())
                ? fileService.save(file)
                : null;

        Menu menu = new Menu();
        menu.setRestaurant(restaurant);
        menu.setName(name);
        menu.setPrice(price);
        menu.setDescription(description);
        menu.setImageUrl(imageUrl);
        menu.setCreatedBy(currentUser);
        // recommendCount, notRecommendCount, reviewCount, avgScore는 기본값(0)으로 시작됨

        Menu saved = menuService.createMenu(menu);
        // JPA로 저장하면 reviewCount=0, avgScore=0.0 기본값으로 DB에 들어갑니다.
        // → 그 결과를 DTO에 매핑해 반환하려면, 간단히 “저장된 엔티티”를 다시 DTO로 바꾸거나,
        //    List 메뉴 조회와 동일한 Mapper를 사용해도 됩니다. (여기서는 createMenu 직후 mapToDto)
        MenuDto dto = mapToDto(saved);
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }

    /**
     * (2) 메뉴 목록 조회 (MyBatis Mapper → MenuDto 반환)
     *
     * - sortBy: price, reviewCount, avgScore 중 하나, 아니면 기본값(restaurant)
     * - direction: ASC or DESC
     */
    @GetMapping
    public List<MenuDto> listMenus(
            @RequestParam(name = "sortBy", required = false, defaultValue = "restaurant") String sortBy,
            @RequestParam(name = "direction", required = false, defaultValue = "ASC") String direction
    ) {
        // 정렬 파라미터를 Map으로 포장
        Map<String, Object> sortParams = new HashMap<>();
        sortParams.put("sortBy", sortBy);
        sortParams.put("direction", direction);

        // MyBatis Mapper를 통해 정렬된 MenuDto 리스트를 직접 가져온다
        return menuService.getAllMenus(sortParams);
    }

    /** (3) 메뉴 상세 조회 (JPA 사용) **/
    @GetMapping("/{id}")
    public ResponseEntity<MenuDto> getMenu(@PathVariable Long id) {
        Menu menu = menuService.getMenuById(id);
        return ResponseEntity.ok(mapToDto(menu));
    }

    /** (4) 특정 메뉴의 리뷰 목록 조회 (JPA 사용) **/
    @GetMapping("/{id}/reviews")
    public List<ReviewDto> listReviews(@PathVariable("id") Long menuId) {
        return reviewService.getReviewsByMenuId(menuId).stream()
                .map(this::mapReviewToDto)
                .collect(Collectors.toList());
    }

    /** (5) 특정 메뉴에 리뷰 생성 (JPA 사용 + 통계 갱신) **/
    @PostMapping(
            value    = "/{id}/reviews",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<ReviewDto> createReview(
            HttpSession session,
            @PathVariable("id") Long menuId,
            @RequestParam int score,
            @RequestParam String content,
            @RequestParam boolean recommend,
            @RequestParam(value = "file", required = false) MultipartFile file
    ) {
        User currentUser = (User) session.getAttribute("loginUser");
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String photoUrl = (file != null && !file.isEmpty())
                ? fileService.save(file)
                : null;

        // 1) REVIEW 엔티티 저장
        Review savedReview = reviewService.createReview(
                menuId,
                new ReviewRequestDto(score, content, recommend),
                currentUser,
                photoUrl
        );

        // 2) 리뷰가 추가되었으므로, 메뉴 테이블에 reviewCount·avgScore를 갱신
        List<Review> allReviews = reviewService.getReviewsByMenuId(menuId);
        int   newCount = allReviews.size();
        double newAvg  = newCount == 0
                ? 0.0
                : allReviews.stream()
                .mapToInt(Review::getScore)
                .average()
                .orElse(0.0);
        newAvg = Math.round(newAvg * 10) / 10.0;
        menuService.updateReviewStats(menuId, newCount, newAvg);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(mapReviewToDto(savedReview));
    }

    /** (6) 메뉴 삭제 (JPA 사용, 본인만) **/
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMenu(
            @PathVariable Long id,
            HttpSession session
    ) throws AccessDeniedException {
        User currentUser = (User) session.getAttribute("loginUser");
        menuService.deleteMenu(id, currentUser);
        return ResponseEntity.ok().build();
    }

    /** (7) 리뷰 삭제 (JPA 사용, 본인만) **/
    @DeleteMapping("/{menuId}/reviews/{reviewId}")
    public ResponseEntity<Void> deleteReview(
            @PathVariable Long menuId,
            @PathVariable Long reviewId,
            HttpSession session
    ) throws AccessDeniedException {
        User currentUser = (User) session.getAttribute("loginUser");
        reviewService.deleteReview(menuId, reviewId, currentUser);

        // 리뷰 삭제 후 메뉴 통계도 갱신
        List<Review> allReviews = reviewService.getReviewsByMenuId(menuId);
        int   newCount = allReviews.size();
        double newAvg  = newCount == 0
                ? 0.0
                : allReviews.stream()
                .mapToInt(Review::getScore)
                .average()
                .orElse(0.0);
        newAvg = Math.round(newAvg * 10) / 10.0;
        menuService.updateReviewStats(menuId, newCount, newAvg);

        return ResponseEntity.ok().build();
    }

    /*―――――― DTO 매핑 헬퍼 ――――――*/

    private MenuDto mapToDto(Menu menu) {
        MenuDto dto = new MenuDto();
        dto.setId(menu.getId());
        dto.setRestaurant(menu.getRestaurant());
        dto.setName(menu.getName());
        dto.setPrice(menu.getPrice());
        dto.setDescription(menu.getDescription());
        dto.setImageUrl(menu.getImageUrl());
        try {
            dto.setCreatedById(menu.getCreatedBy().getId());
            dto.setCreatedByUsername(menu.getCreatedBy().getUsername());
        } catch (Exception e) {
            dto.setCreatedById(null);
            dto.setCreatedByUsername(null);
        }
        dto.setRecommendCount(menu.getRecommendCount());
        dto.setNotRecommendCount(menu.getNotRecommendCount());

        // ▶ 이제 엔티티에 저장된 값을 그대로 꺼내서 DTO에 세팅
        dto.setReviewCount(menu.getReviewCount());
        dto.setAvgScore(menu.getAvgScore());

        return dto;
    }

    private ReviewDto mapReviewToDto(Review r) {
        ReviewDto dto = new ReviewDto();
        dto.setId(r.getId());
        dto.setMenuId(r.getMenu().getId());
        dto.setUserId(r.getUser().getId());
        dto.setUserName(r.getUserName());
        dto.setScore(r.getScore());
        dto.setContent(r.getContent());
        dto.setRecommend(r.isRecommend());
        dto.setPhotoUrl(r.getPhotoUrl());
        dto.setLikeCount(r.getLikeCount());
        dto.setCreatedAt(r.getCreatedAt());
        return dto;
    }
    @PostMapping("/{menuId}/reviews/{reviewId}/like")
    public ResponseEntity<Void> likeReview(
            @PathVariable Long menuId,
            @PathVariable Long reviewId
    ) {
        reviewService.incrementLikeCount(reviewId);
        return ResponseEntity.ok().build();
    }
}
