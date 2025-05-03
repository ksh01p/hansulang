package com.example.demo.controller.page;

import com.example.demo.domain.User;
import com.example.demo.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller

/**
 * 5/2(금) 김수형
 * 로그인, 회원가입 등 권한 부여가 필요한 로그인, 회원가입 등에 필요한 핸드링 목적 pagecontroller
 */
public class AuthController {

    // 사용자 관련 로직(회원가입, 로그인 등)을 처리하는 서비스 클래스
    private final UserService userService;

    // 생성자를 통해 UserService 객체를 주입받습니다 (Spring이 자동으로 관리해줍니다)
    public AuthController(UserService userService) {
        this.userService = userService;
    }

    /**
     * 홈페이지 접속 처리
     * - "/" 또는 "/index" 주소로 접속 시 실행됩니다.
     * - 로그인한 사용자 정보를 세션에서 꺼내서, 모델에 담아 HTML로 전달합니다.
     * - templates/index.html을 렌더링합니다.
     */
    @GetMapping({"/", "/index"})
    public String home(HttpSession session, Model model) {
        // 세션에 저장된 로그인 사용자 정보 꺼내기
        User user = (User) session.getAttribute("loginUser");
        // 모델에 로그인 사용자 정보 추가
        model.addAttribute("loginUser", user);
        // index.html 파일을 사용자에게 보여줌
        return "index";
    }

    /**
     * 회원가입 페이지로 이동
     * - "/register" GET 요청이 들어오면 register.html을 보여줍니다.
     */
    @GetMapping("/register")
    public String registerForm() {
        return "register";
    }

    /**
     * 회원가입 처리
     * - "/register" POST 요청이 들어오면 실행됩니다.
     * - 전달받은 username, password, role로 회원가입을 진행한 후, 로그인 페이지로 리다이렉트합니다.
     */
    @PostMapping("/register")
    public String register(@RequestParam String username,
                           @RequestParam String password,
                           @RequestParam(defaultValue = "USER") String role) {
        userService.register(username, password, role); // 회원가입 실행
        return "redirect:/login"; // 로그인 페이지로 이동
    }

    /**
     * 로그인 페이지로 이동
     * - "/login" GET 요청
     * - 에러 파라미터가 존재하면 로그인 실패 메시지를 모델에 추가
     */
    @GetMapping("/login")
    public String loginForm(@RequestParam(name = "error", required = false) String error,
                            Model model) {
        if (error != null) {
            model.addAttribute("loginError", "아이디 또는 비밀번호가 올바르지 않습니다.");
        }
        return "login"; // login.html 렌더링
    }

    /**
     * 로그인 처리
     * - "/login" POST 요청
     * - 사용자 정보를 확인하고 세션에 로그인 정보를 저장한 뒤, 홈페이지로 리다이렉트
     */
    @PostMapping("/login")
    public String login(@RequestParam String username,
                        @RequestParam String password,
                        HttpSession session) {
        // 로그인 서비스 실행 → 아이디/비번이 맞는 경우 유저 객체 반환
        User user = userService.login(username, password);
        // 로그인된 사용자 정보를 세션에 저장
        session.setAttribute("loginUser", user);
        return "redirect:/"; // 홈으로 이동
    }

    /**
     * 로그아웃 처리
     * - "/logout" GET 요청
     * - 세션을 초기화하여 로그아웃하고, 로그인 페이지로 이동
     */
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate(); // 세션 전체 초기화 (로그아웃)
        return "redirect:/login"; // 로그인 페이지로 이동
    }
}
