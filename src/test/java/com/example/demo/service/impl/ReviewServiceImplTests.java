package com.example.demo.service.impl;

import com.example.demo.domain.Menu;
import com.example.demo.domain.Review;
import com.example.demo.domain.User;
import com.example.demo.dto.ReviewRequestDto;
import com.example.demo.repository.MenuRepository;
import com.example.demo.repository.ReviewRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ReviewServiceImplTest {

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private MenuRepository menuRepository;

    @InjectMocks
    private ReviewServiceImpl reviewService;

    private final Long menuId = 1L;
    private final String photoUrl = "photo.jpg";
    private Menu mockMenu;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        mockMenu = new Menu();
        mockMenu.setId(menuId);
        mockMenu.setName("불고기덮밥");
    }

    @Test
    void testCreateReview_NormalRecommendReview_Success() {
        ReviewRequestDto dto = createDto(5, "맛있어요", true);
        User user = createUser("reviewer");

        when(menuRepository.findById(menuId)).thenReturn(java.util.Optional.of(mockMenu));
        when(reviewRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        Review result = reviewService.createReview(menuId, dto, user, photoUrl);

        assertNotNull(result);
        assertTrue(result.isRecommend());
        assertEquals("맛있어요", result.getContent());
    }

    @Test
    void testCreateReview_NormalNotRecommendReview_Success() {
        ReviewRequestDto dto = createDto(2, "별로에요", false);
        User user = createUser("reviewer");

        when(menuRepository.findById(menuId)).thenReturn(java.util.Optional.of(mockMenu));
        when(reviewRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        Review result = reviewService.createReview(menuId, dto, user, photoUrl);

        assertNotNull(result);
        assertFalse(result.isRecommend());
        assertEquals("별로에요", result.getContent());
    }

    @Test
    void testCreateReview_EmptyContent_ShouldFail() {
        ReviewRequestDto dto = createDto(3, "", true);
        User user = createUser("reviewer");

        when(menuRepository.findById(menuId)).thenReturn(java.util.Optional.of(mockMenu));

        assertThrows(IllegalArgumentException.class, () -> {
            reviewService.createReview(menuId, dto, user, photoUrl);
        });
    }

    @Test
    void testCreateReview_MenuNotFound_ShouldThrow() {
        ReviewRequestDto dto = createDto(4, "좋아요", true);
        User user = createUser("reviewer");

        when(menuRepository.findById(menuId)).thenReturn(java.util.Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> {
            reviewService.createReview(menuId, dto, user, photoUrl);
        });
    }

    @Test
    void testCreateReview_MissingUsername_ShouldFail() {
        ReviewRequestDto dto = createDto(5, "훌륭합니다", true);
        User user = new User();
        user.setId(123L);
        user.setUsername(null); // 사용자 이름 없음

        when(menuRepository.findById(menuId)).thenReturn(java.util.Optional.of(mockMenu));

        assertThrows(IllegalArgumentException.class, () -> {
            reviewService.createReview(menuId, dto, user, photoUrl);
        });
    }

    // 유틸 함수들
    private ReviewRequestDto createDto(int score, String content, boolean recommend) {
        ReviewRequestDto dto = new ReviewRequestDto();
        dto.setScore(score);
        dto.setContent(content);
        dto.setRecommend(recommend);
        return dto;
    }

    private User createUser(String username) {
        User user = new User();
        user.setId(100L);
        user.setUsername(username);
        return user;
    }
}
