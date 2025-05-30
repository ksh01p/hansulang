package com.example.demo.integration;

import com.example.demo.dto.LoginRequest;
import com.example.demo.dto.RegisterRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AuthIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private ObjectMapper objectMapper;

    @Value("${test-user.password}")
    private String password;

    private final String baseUrl = "/api/auth";

    @Test
    void fullAuthFlow() throws Exception {
        WebTestClient client = WebTestClient.bindToServer()
                .baseUrl("http://localhost:" + port).build();

        String username = "testuser_" + System.currentTimeMillis() + "@handong.ac.kr"; // 중복 방지용
        String role = "USER";

        // 1. 회원가입 요청
        RegisterRequest register = new RegisterRequest();
        register.setUsername(username);
        register.setPassword(password);
        register.setRole(role);

        client.post()
                .uri(baseUrl + "/register")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(objectMapper.writeValueAsString(register))
                .exchange()
                .expectStatus().isOk();

        // 2. 로그인 요청
        LoginRequest login = new LoginRequest();
        login.setUsername(username);
        login.setPassword(password);

        WebTestClient.ResponseSpec loginResponse = client.post()
                .uri(baseUrl + "/login")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(objectMapper.writeValueAsString(login))
                .exchange()
                .expectStatus().isOk();

        // 3. 세션 쿠키 추출 후 /me 요청
        String sessionCookie = loginResponse.returnResult(String.class)
                .getResponseCookies()
                .getFirst("JSESSIONID")
                .getValue();

        WebTestClient clientWithSession = client.mutate()
                .defaultCookie("JSESSIONID", sessionCookie)
                .build();

        clientWithSession.get()
                .uri(baseUrl + "/me")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.username").isEqualTo(username);

        // 4. 로그아웃
        clientWithSession.post()
                .uri(baseUrl + "/logout")
                .exchange()
                .expectStatus().isOk();

        // 5. 로그아웃 후 /me 요청 → 401 기대
        clientWithSession.get()
                .uri(baseUrl + "/me")
                .exchange()
                .expectStatus().isUnauthorized();
    }
}