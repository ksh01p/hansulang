// src/main/java/com/example/demo/controller/rest/MenuRestController.java
package com.example.demo.controller;

import com.example.demo.domain.Menu;
import com.example.demo.domain.Review;
import com.example.demo.service.MenuService;
import com.example.demo.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/menus")
@RequiredArgsConstructor
public class MenuRestController {

    private final MenuService menuService;
    private final ReviewService reviewService;

    // 1) 메뉴 등록
    @PostMapping
    public Menu createMenu(@RequestBody Menu menu) {
        return menuService.createMenu(menu);
    }

    // 2) 메뉴 목록
    @GetMapping
    public List<Menu> listMenus() {
        return menuService.getAllMenus();
    }

    // 3) 메뉴 상세
    @GetMapping("/{id}")
    public Menu getMenu(@PathVariable Long id) {
        return menuService.getMenuById(id);
    }

    // 4) 특정 메뉴의 리뷰 목록
    @GetMapping("/{id}/reviews")
    public List<Review> listReviews(@PathVariable("id") Long menuId) {
        return reviewService.getReviewsByMenuId(menuId);
    }

    // 5) 특정 메뉴에 리뷰 생성
    @PostMapping("/{id}/reviews")
    public Review createReview(
            @PathVariable("id") Long menuId,
            @RequestBody Review review
    ) {
        return reviewService.createReview(menuId, review);
    }
}
