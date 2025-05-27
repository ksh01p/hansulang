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
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/menus")
@RequiredArgsConstructor
public class MenuRestController {

    private final MenuService    menuService;
    private final ReviewService  reviewService;
    private final FileService    fileService;

    /** 1) 메뉴 등록 **/
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

        Menu saved = menuService.createMenu(menu);
        return ResponseEntity.status(HttpStatus.CREATED).body(mapToDto(saved));
    }

    /** 2) 메뉴 목록 조회 **/
    @GetMapping
    public List<MenuDto> listMenus() {
        return menuService.getAllMenus().stream()
                .map(menu -> {
                    // Map basic Menu → MenuDto
                    MenuDto dto = mapToDto(menu);

                    // load reviews for metrics
                    List<Review> reviews = reviewService.getReviewsByMenuId(menu.getId());
                    int count = reviews.size();
                    double avg = count == 0
                            ? 0.0
                            : reviews.stream()
                            .mapToInt(Review::getScore)
                            .average()
                            .orElse(0.0);

                    dto.setReviewCount(count);
                    dto.setAvgScore(Math.round(avg * 10) / 10.0); // 소수점 첫째자리까지 반올림
                    return dto;
                })
                .collect(Collectors.toList());
    }
    /** 3) 메뉴 상세 조회 **/
    @GetMapping("/{id}")
    public ResponseEntity<MenuDto> getMenu(@PathVariable Long id) {
        Menu menu = menuService.getMenuById(id);
        return ResponseEntity.ok(mapToDto(menu));
    }

    /** 4) 특정 메뉴의 리뷰 목록 조회 **/
    @GetMapping("/{id}/reviews")
    public List<ReviewDto> listReviews(@PathVariable("id") Long menuId) {
        return reviewService.getReviewsByMenuId(menuId).stream()
                .map(this::mapReviewToDto)
                .collect(Collectors.toList());
    }

    /** 5) 특정 메뉴에 리뷰 생성 **/
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

        Review saved = reviewService.createReview(
                menuId,
                new ReviewRequestDto(score, content, recommend),
                currentUser,
                photoUrl
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(mapReviewToDto(saved));
    }

    /** 6) 메뉴 삭제 (본인만) **/
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMenu(
            @PathVariable Long id,
            HttpSession session
    ) throws AccessDeniedException {
        User currentUser = (User) session.getAttribute("loginUser");
        menuService.deleteMenu(id, currentUser);
        return ResponseEntity.ok().build();
    }

    /** 7) 리뷰 삭제 (본인만) **/
    @DeleteMapping("/{menuId}/reviews/{reviewId}")
    public ResponseEntity<Void> deleteReview(
            @PathVariable Long menuId,
            @PathVariable Long reviewId,
            HttpSession session
    ) throws AccessDeniedException {
        User currentUser = (User) session.getAttribute("loginUser");
        reviewService.deleteReview(menuId, reviewId, currentUser);
        return ResponseEntity.ok().build();
    }

    /*--- DTO 매핑 헬퍼 ---*/

    private MenuDto mapToDto(Menu menu) {
        MenuDto dto = new MenuDto();
        dto.setId(menu.getId());
        dto.setRestaurant(menu.getRestaurant());
        dto.setName(menu.getName());
        dto.setPrice(menu.getPrice());
        dto.setDescription(menu.getDescription());
        dto.setImageUrl(menu.getImageUrl());
        dto.setCreatedById(menu.getCreatedBy().getId());
        dto.setCreatedByUsername(menu.getCreatedBy().getUsername());
        dto.setRecommendCount(reviewService.countRecommend(menu.getId()));
        dto.setNotRecommendCount(reviewService.countNotRecommend(menu.getId()));
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
}
