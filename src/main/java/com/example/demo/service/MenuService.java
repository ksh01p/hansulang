// src/main/java/com/example/demo/service/MenuService.java
package com.example.demo.service;

import com.example.demo.domain.Menu;
import java.util.List;

public interface MenuService {
    Menu createMenu(Menu menu);
    List<Menu> getAllMenus();
    Menu getMenuById(Long id);
}
