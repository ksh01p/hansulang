// src/main/java/com/example/demo/service/impl/MenuServiceImpl.java
package com.example.demo.service.impl;

import com.example.demo.domain.Menu;
import com.example.demo.repository.MenuRepository;
import com.example.demo.service.MenuService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MenuServiceImpl implements MenuService {
    private final MenuRepository menuRepo;

    @Override
    public Menu createMenu(Menu menu) {
        return menuRepo.save(menu);
    }

    @Override
    public List<Menu> getAllMenus() {
        return menuRepo.findAll();
    }

    @Override
    public Menu getMenuById(Long id) {
        return menuRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid menu id: " + id));
    }
}
