package com.example.demo.service.impl;

import com.example.demo.domain.Menu;
import com.example.demo.domain.User;
import com.example.demo.repository.MenuRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MenuServiceImplTest {

    @Mock
    private MenuRepository menuRepository;

    @InjectMocks
    private MenuServiceImpl menuService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateMenu_든든한동() {
        testMenuSave("든든한동", "김치찌개", 5500.0, "매콤한 찌개");
    }

    @Test
    void testCreateMenu_따스한동() {
        testMenuSave("따스한동", "제육볶음", 6000.0, "매운 제육");
    }

    @Test
    void testCreateMenu_맘스키친() {
        testMenuSave("맘스키친", "돈까스", 5800.0, "바삭한 돈까스");
    }

    @Test
    void testCreateMenu_HPlate() {
        testMenuSave("H:plate", "스파게티", 6200.0, "토마토 스파게티");
    }

    @Test
    void testCreateMenu_AsianMarket() {
        testMenuSave("Asian Market", "팟타이", 6300.0, "쫄깃한 팟타이");
    }

    private void testMenuSave(String restaurant, String name, double price, String description) {
        // given
        User mockUser = new User();
        mockUser.setId(1L);
        mockUser.setUsername("admin");

        Menu menu = new Menu();
        menu.setRestaurant(restaurant);
        menu.setName(name);
        menu.setPrice(price);
        menu.setDescription(description);
        menu.setImageUrl("sample.jpg");
        menu.setCreatedBy(mockUser);

        when(menuRepository.save(any(Menu.class))).thenReturn(menu);

        // when
        Menu result = menuService.createMenu(menu);

        // then
        assertNotNull(result);
        verify(menuRepository, times(1)).save(menu);
        assertEquals(restaurant, result.getRestaurant());
        assertEquals(name, result.getName());
        assertEquals(price, result.getPrice());
        assertEquals(description, result.getDescription());

        // mock 초기화 (다음 테스트에 영향 방지)
        reset(menuRepository);
    }
}
