// src/main/java/com/example/demo/controller/rest/MenuRestController.java
package com.example.demo.controller;

import com.example.demo.domain.Menu;
import com.example.demo.domain.Review;
import com.example.demo.dto.ReviewDto;
import com.example.demo.dto.ReviewRequestDto;
import com.example.demo.service.FileService;
import com.example.demo.service.MenuService;
import com.example.demo.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/menus")
@RequiredArgsConstructor
public class MenuRestController {

    private final MenuService menuService;
    private final ReviewService reviewService;
    private final FileService fileService;  // ← 이 줄 추가


    // 1) 메뉴 등록
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Menu> createMenu(
            @RequestParam String name,
            @RequestParam Double price,
            @RequestParam String description,
            @RequestParam(value = "file", required = false) MultipartFile file
    ) {
        // 파일을 저장하고 파일 URL을 반환하는 로직 (예: S3 or 로컬 저장)
        String imageUrl = file != null && !file.isEmpty()
                ? fileService.save(file)
                : null;


        Menu menu = new Menu();
        menu.setName(name);
        menu.setPrice(price);
        menu.setDescription(description);
        menu.setImageUrl(imageUrl);

        Menu saved = menuService.createMenu(menu);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    // 2) 메뉴 목록 조회
    @GetMapping
    public List<Menu> listMenus() {
        return menuService.getAllMenus();
    }

    // 3) 메뉴 상세 조회
    @GetMapping("/{id}")
    public ResponseEntity<Menu> getMenu(@PathVariable Long id) {
        Menu menu = menuService.getMenuById(id);
        return ResponseEntity.ok(menu);
    }

    // 4) 특정 메뉴의 리뷰 목록 조회
    @GetMapping("/{id}/reviews")
    public List<ReviewDto> listReviews(@PathVariable("id") Long menuId) {
        return reviewService.getReviewsByMenuId(menuId).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    // 5) 특정 메뉴에 리뷰 생성
    @PostMapping("/{id}/reviews")
    public ResponseEntity<ReviewDto> createReview(
            @PathVariable("id") Long menuId,
            @RequestBody ReviewRequestDto dto
    ) {
        Review saved = reviewService.createReview(menuId, dto);
        ReviewDto response = mapToDto(saved);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // 엔티티 → 응답 DTO 변환 헬퍼
    private ReviewDto mapToDto(Review r) {
        return new ReviewDto(
                r.getId(),
                r.getMenu().getId(),
                r.getUserName(),
                r.getScore(),
                r.getTitle(),
                r.getContent(),
                r.isRecommend(),
                r.getCreatedAt()
        );
    }
}