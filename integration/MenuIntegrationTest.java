package com.example.demo.integration;

import com.example.demo.dto.ApiResponse;
import com.example.demo.dto.MenuDto;
import com.example.demo.dto.RegisterRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class MenuIntegrationTest {

    @LocalServerPort
    int port;

    @Value("${test-user.password}")
    String testPassword;

    private String baseUrl;
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private String sessionId;

    @BeforeEach
    void setUp() {
        baseUrl = "http://localhost:" + port;

        // 회원가입 시도
        RegisterRequest request = new RegisterRequest();
        request.setUsername("testuser@handong.ac.kr");
        request.setPassword(testPassword);
        request.setRole("USER");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<RegisterRequest> entity = new HttpEntity<>(request, headers);

        try {
            restTemplate.postForEntity(baseUrl + "/api/auth/register", entity, ApiResponse.class);
        } catch (Exception ignored) {
            // 이미 등록된 사용자일 경우 무시
        }

        // 로그인하여 세션 유지
        MultiValueMap<String, String> loginForm = new LinkedMultiValueMap<>();
        loginForm.add("username", "testuser@handong.ac.kr");
        loginForm.add("password", testPassword);

        HttpHeaders loginHeaders = new HttpHeaders();
        loginHeaders.setContentType(MediaType.APPLICATION_JSON);
        String loginJson = String.format("{\"username\":\"%s\",\"password\":\"%s\"}",
                "testuser@handong.ac.kr", testPassword);
        HttpEntity<String> loginEntity = new HttpEntity<>(loginJson, loginHeaders);
        ResponseEntity<String> loginResponse = restTemplate.postForEntity(baseUrl + "/api/auth/login", loginEntity, String.class);

        List<String> cookies = loginResponse.getHeaders().get(HttpHeaders.SET_COOKIE);
        if (cookies != null && !cookies.isEmpty()) {
            sessionId = cookies.get(0).split(";", 2)[0];
        }
    }

    @Test
    public void testMenuRegistrationAndListingFlow() {
        // 1. 메뉴 등록
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        headers.set(HttpHeaders.COOKIE, sessionId);

        MultiValueMap<String, Object> form = new LinkedMultiValueMap<>();
        form.add("restaurant", "학생식당");
        form.add("name", "제육볶음");
        form.add("price", 4500);
        form.add("description", "매콤한 제육");

        HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(form, headers);

        ResponseEntity<MenuDto> response = restTemplate.postForEntity(
                baseUrl + "/api/menus",
                request,
                MenuDto.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getName()).isEqualTo("제육볶음");

        // 2. 전체 메뉴 조회
        HttpHeaders getHeaders = new HttpHeaders();
        getHeaders.set(HttpHeaders.COOKIE, sessionId);

        HttpEntity<Void> entity = new HttpEntity<>(getHeaders);

        ResponseEntity<List<MenuDto>> listResponse = restTemplate.exchange(
                baseUrl + "/api/menus",
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<>() {}
        );

        assertThat(listResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        List<MenuDto> menus = listResponse.getBody();
        assertThat(menus).isNotEmpty();
        assertThat(menus.stream().anyMatch(m -> m.getName().equals("제육볶음"))).isTrue();
    }
}
