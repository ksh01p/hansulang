package com.example.demo.integration;

import com.example.demo.dto.*;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class MenuStatsAndReviewDeletionTest {

    @LocalServerPort
    private int port;

    private String baseUrl;
    private RestTemplate rest;

    @Value("${test-user.password}")
    private String password;

    @BeforeEach
    void setup() {
        this.baseUrl = "http://localhost:" + port;
        this.rest = new RestTemplateBuilder()
                .requestFactory(() -> new HttpComponentsClientHttpRequestFactory(HttpClients.custom().build()))
                .build();
    }

    @Test
    void MenuReviewCheck_DeleteReview() {
        String username = "user_" + UUID.randomUUID() + "@handong.ac.kr";

        // 1. 회원가입
        RegisterRequest register = new RegisterRequest(username, password, "USER");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        rest.exchange(baseUrl + "/api/auth/register", HttpMethod.POST, new HttpEntity<>(register, headers), Void.class);

        // 2. 로그인
        LoginRequest login = new LoginRequest(username, password);
        ResponseEntity<UserDto> loginResp = rest.exchange(
                baseUrl + "/api/auth/login",
                HttpMethod.POST,
                new HttpEntity<>(login, headers),
                UserDto.class
        );
        assertEquals(HttpStatus.OK, loginResp.getStatusCode());
        String sessionCookie = loginResp.getHeaders().getFirst(HttpHeaders.SET_COOKIE);
        assertNotNull(sessionCookie);

        // 3. 메뉴 등록
        String menuName = "된장찌개_" + UUID.randomUUID();
        String restaurant = "기숙사식당_" + UUID.randomUUID();

        MultiValueMap<String, Object> menuData = new LinkedMultiValueMap<>();
        menuData.add("restaurant", restaurant);
        menuData.add("name", menuName);
        menuData.add("price", "6500");
        menuData.add("description", "구수한 된장찌개");

        HttpHeaders multipartHeaders = new HttpHeaders();
        multipartHeaders.setContentType(MediaType.MULTIPART_FORM_DATA);
        multipartHeaders.add("Cookie", sessionCookie);
        ResponseEntity<MenuDto> menuResp = rest.postForEntity(
                baseUrl + "/api/menus",
                new HttpEntity<>(menuData, multipartHeaders),
                MenuDto.class
        );
        assertEquals(HttpStatus.CREATED, menuResp.getStatusCode());
        Long menuId = menuResp.getBody().getId();

        // 4. 리뷰 작성
        MultiValueMap<String, Object> reviewData = new LinkedMultiValueMap<>();
        reviewData.add("score", "4");
        reviewData.add("content", "구수했어요");
        reviewData.add("recommend", "true");

        ResponseEntity<ReviewDto> reviewResp = rest.postForEntity(
                baseUrl + "/api/menus/" + menuId + "/reviews",
                new HttpEntity<>(reviewData, multipartHeaders),
                ReviewDto.class
        );
        assertEquals(HttpStatus.CREATED, reviewResp.getStatusCode());
        Long reviewId = reviewResp.getBody().getId();

        // ✅ 5. 메뉴 목록 조회 → 통계 확인
        HttpHeaders getHeaders = new HttpHeaders();
        getHeaders.add("Cookie", sessionCookie);
        ResponseEntity<MenuDto[]> menusResp = rest.exchange(
                baseUrl + "/api/menus",
                HttpMethod.GET,
                new HttpEntity<>(getHeaders),
                MenuDto[].class
        );
        assertEquals(HttpStatus.OK, menusResp.getStatusCode());

        MenuDto matched = Arrays.stream(menusResp.getBody())
                .filter(m -> m.getId().equals(menuId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("메뉴 없음"));

        assertEquals(1, matched.getReviewCount());
        assertEquals(1, matched.getRecommendCount());
        assertEquals(0, matched.getNotRecommendCount());
        assertEquals(4.0, matched.getAvgScore());

        // ✅ 6. 리뷰 삭제
        rest.exchange(
                baseUrl + "/api/menus/" + menuId + "/reviews/" + reviewId,
                HttpMethod.DELETE,
                new HttpEntity<>(getHeaders),
                Void.class
        );

        // 삭제 후 리뷰 리스트가 비어야 함
        ResponseEntity<ReviewDto[]> afterDelete = rest.exchange(
                baseUrl + "/api/menus/" + menuId + "/reviews",
                HttpMethod.GET,
                new HttpEntity<>(getHeaders),
                ReviewDto[].class
        );

        assertEquals(HttpStatus.OK, afterDelete.getStatusCode());
        assertNotNull(afterDelete.getBody());
        assertEquals(0, afterDelete.getBody().length);
    }
}
