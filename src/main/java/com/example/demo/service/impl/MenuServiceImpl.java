// src/main/java/com/example/demo/service/impl/MenuServiceImpl.java
package com.example.demo.service.impl;

import com.example.demo.dto.MenuDto;
import com.example.demo.domain.Menu;
import com.example.demo.domain.User;
import com.example.demo.mapper.MenuMapper;
import com.example.demo.repository.MenuRepository;
import com.example.demo.service.MenuService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class MenuServiceImpl implements MenuService {

    private final MenuRepository menuRepo;  // 기존 JPA Repository
    private final MenuMapper     menuMapper; // MyBatis Mapper

    @Override
    public Menu createMenu(Menu menu) {
        return menuRepo.save(menu);
    }

    /**
     * MyBatis Mapper를 호출하여, 정렬 기준에 맞는 MenuDto 리스트를 반환
     */
    @Override
    public List<MenuDto> getAllMenus(Map<String, Object> sortParams) {
        return menuMapper.findAllMenus(sortParams);
    }

    @Override
    public Menu getMenuById(Long id) {
        return menuRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid menu id: " + id));
    }

    @Override
    @Transactional
    public void deleteMenu(Long id, User currentUser) throws AccessDeniedException {
        Menu menu = menuRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("메뉴 없음: " + id));
        if (!menu.getCreatedBy().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("본인의 메뉴만 삭제할 수 있습니다.");
        }
        menuRepo.delete(menu);
    }

    /**
     * 리뷰가 추가·삭제될 때, menu_items.review_count와 menu_items.avg_score 컬럼을 업데이트
     */
    @Override
    @Transactional
    public void updateReviewStats(Long menuId, int newReviewCount, double newAvgScore) {
        Menu menu = menuRepo.findById(menuId)
                .orElseThrow(() -> new IllegalArgumentException("메뉴 없음: " + menuId));
        menu.setReviewCount(newReviewCount);
        menu.setAvgScore(newAvgScore);
        menuRepo.save(menu);
    }
}
