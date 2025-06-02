// src/main/java/com/example/demo/service/MenuService.java
package com.example.demo.service;

import com.example.demo.dto.MenuDto;
import com.example.demo.domain.Menu;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.Map;

public interface MenuService {
    Menu createMenu(Menu menu);
    List<MenuDto> getAllMenus(Map<String, Object> sortParams);
    Menu getMenuById(Long id);
    void deleteMenu(Long id, com.example.demo.domain.User currentUser) throws AccessDeniedException;
    void updateReviewStats(Long menuId, int newReviewCount, double newAvgScore);
}
