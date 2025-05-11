// src/main/java/com/example/demo/controller/AuthRestController.java
package com.example.demo.controller;

import com.example.demo.domain.User;
import com.example.demo.dto.ApiResponse;
import com.example.demo.dto.LoginRequest;
import com.example.demo.dto.RegisterRequest;
import com.example.demo.dto.UserDto;
import com.example.demo.service.UserService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthRestController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse> register(@RequestBody RegisterRequest req) {
        try {
            userService.register(req.getUsername(), req.getPassword(), req.getRole());
            return ResponseEntity.ok(new ApiResponse(true, "회원가입 성공"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(new ApiResponse(false, e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest req, HttpSession session) {
        try {
            User user = userService.login(req.getUsername(), req.getPassword());
            session.setAttribute("loginUser", user);
            UserDto dto = new UserDto(user.getId(), user.getUsername(), user.getRole());
            return ResponseEntity.ok(dto);
        } catch (IllegalArgumentException e) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse(false, "아이디 또는 비밀번호가 올바르지 않습니다."));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse> logout(HttpSession session) {
        session.invalidate();
        return ResponseEntity.ok(new ApiResponse(true, "로그아웃 되었습니다."));
    }

    /**
     * 현재 세션에 저장된 로그인 유저 반환
     */
    @GetMapping("/me")
    public ResponseEntity<UserDto> me(HttpSession session) {
        User user = (User) session.getAttribute("loginUser");
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        UserDto dto = new UserDto(user.getId(), user.getUsername(), user.getRole());
        return ResponseEntity.ok(dto);
    }
}
