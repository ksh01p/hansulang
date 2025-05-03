package com.example.demo.controller;

import com.example.demo.domain.User;
import com.example.demo.dto.ApiResponse;
import com.example.demo.dto.LoginRequest;
import com.example.demo.dto.RegisterRequest;
import com.example.demo.dto.UserDto;
import com.example.demo.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 5/2(금) 김수형
 *
 * 이 RestController는 REST 방식으로 로그인/회원가입/로그아웃을 처리하는 API 전용 RestContoller
 * 요청과 응답은 HTML이 아닌 JSON 형태로 주고받습니다.
 */
@RestController
@RequestMapping("/api/auth")
public class AuthRestController {

    // 사용자 관련 비즈니스 로직을 처리하는 서비스 클래스
    private final UserService userService;

    // 생성자를 통해 userService 의존성을 주입받습니다.
    public AuthRestController(UserService userService) {
        this.userService = userService;
    }

    /**
     * 회원가입 API
     * - 클라이언트에서 username, password, role 정보를 JSON 형태로 POST하면 회원가입 처리
     * - 성공 시 200 OK와 메시지를 반환, 실패 시 409 CONFLICT와 에러 메시지 반환
     */
    @PostMapping("/register")
    public ResponseEntity<ApiResponse> register(@RequestBody RegisterRequest req) {
        try {
            userService.register(req.getUsername(), req.getPassword(), req.getRole());
            return ResponseEntity.ok(new ApiResponse(true, "회원가입 성공"));
        } catch (IllegalArgumentException e) {
            // 예: 중복된 사용자 이름이 있을 경우 예외 발생
            return ResponseEntity
                    .status(HttpStatus.CONFLICT) // 409 에러: 자원 충돌
                    .body(new ApiResponse(false, e.getMessage()));
        }
    }

    /**
     * 로그인 API
     * - username, password를 JSON으로 받아서 인증 처리
     * - 성공 시 UserDto(JSON) 반환 + 세션 저장
     * - 실패 시 401 UNAUTHORIZED와 에러 메시지 반환
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest req, HttpSession session) {
        try {
            // 로그인 성공 시 사용자 정보 반환
            User user = userService.login(req.getUsername(), req.getPassword());
            // 세션에 사용자 정보 저장 (인증된 상태 유지)
            session.setAttribute("loginUser", user);
            // 프론트에 보낼 응답은 필요한 필드만 추린 UserDto
            UserDto dto = new UserDto(user.getId(), user.getUsername(), user.getRole());
            return ResponseEntity.ok(dto);
        } catch (IllegalArgumentException e) {
            // 로그인 실패 시
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED) // 401 에러: 인증 실패
                    .body(new ApiResponse(false, "아이디 또는 비밀번호가 올바르지 않습니다."));
        }
    }

    /**
     * 로그아웃 API
     * - 세션을 제거하여 사용자 로그아웃 처리
     * - 항상 200 OK 반환
     */
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse> logout(HttpSession session) {
        // 세션 무효화 → 로그인 정보 삭제
        session.invalidate();
        return ResponseEntity.ok(new ApiResponse(true, "로그아웃 되었습니다."));
    }
}
