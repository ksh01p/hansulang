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

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ReviewFlowIntegrationTest {

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
    void registering__menu_writing_review_viewing_it() {
        String username = "user_" + UUID.randomUUID() + "@handong.ac.kr";

        // 1. 회원가입
        RegisterRequest register = new RegisterRequest(username, password, "USER");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<RegisterRequest> registerEntity = new HttpEntity<>(register, headers);
        rest.exchange(baseUrl + "/api/auth/register", HttpMethod.POST, registerEntity, Void.class);

        // 2. 로그인
        LoginRequest login = new LoginRequest(username, password);
        HttpEntity<LoginRequest> loginEntity = new HttpEntity<>(login, headers);
        ResponseEntity<UserDto> loginResp = rest.exchange(
                baseUrl + "/api/auth/login",
                HttpMethod.POST,
                loginEntity,
                UserDto.class
        );

        assertEquals(HttpStatus.OK, loginResp.getStatusCode());
        String sessionCookie = loginResp.getHeaders().getFirst(HttpHeaders.SET_COOKIE);
        assertNotNull(sessionCookie);

        // 3. 메뉴 등록
        String menuName = "김치찌개_" + UUID.randomUUID();
        String restaurant = "학생식당_" + UUID.randomUUID();

        MultiValueMap<String, Object> menuData = new LinkedMultiValueMap<>();
        menuData.add("restaurant", restaurant);
        menuData.add("name", menuName);
        menuData.add("price", "5500");
        menuData.add("description", "매콤한 김치찌개");

        HttpHeaders menuHeaders = new HttpHeaders();
        menuHeaders.setContentType(MediaType.MULTIPART_FORM_DATA);
        menuHeaders.add("Cookie", sessionCookie);
        HttpEntity<MultiValueMap<String, Object>> menuRequest = new HttpEntity<>(menuData, menuHeaders);

        ResponseEntity<MenuDto> menuResp = rest.postForEntity(baseUrl + "/api/menus", menuRequest, MenuDto.class);
        assertEquals(HttpStatus.CREATED, menuResp.getStatusCode());
        Long menuId = menuResp.getBody().getId();

        // 4. 리뷰 작성
        MultiValueMap<String, Object> reviewData = new LinkedMultiValueMap<>();
        reviewData.add("score", "5");
        reviewData.add("content", "정말 맛있어요!");
        reviewData.add("recommend", "true");

        HttpEntity<MultiValueMap<String, Object>> reviewEntity = new HttpEntity<>(reviewData, menuHeaders);
        ResponseEntity<ReviewDto> reviewResp = rest.postForEntity(
                baseUrl + "/api/menus/" + menuId + "/reviews",
                reviewEntity,
                ReviewDto.class
        );

        assertEquals(HttpStatus.CREATED, reviewResp.getStatusCode());
        assertEquals("정말 맛있어요!", reviewResp.getBody().getContent());

        // 5. 리뷰 조회
        HttpHeaders getHeaders = new HttpHeaders();
        getHeaders.add("Cookie", sessionCookie);
        HttpEntity<Void> getEntity = new HttpEntity<>(getHeaders);
        ResponseEntity<ReviewDto[]> reviewListResp = rest.exchange(
                baseUrl + "/api/menus/" + menuId + "/reviews",
                HttpMethod.GET,
                getEntity,
                ReviewDto[].class
        );

        assertEquals(HttpStatus.OK, reviewListResp.getStatusCode());
        assertNotNull(reviewListResp.getBody());
        assertTrue(reviewListResp.getBody().length > 0);
    }
}
