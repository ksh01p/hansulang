package com.example.demo.controller.page;

import com.example.demo.domain.User;
import com.example.demo.service.UserService;
import com.example.demo.service.MenuService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class AuthController {

    private final UserService userService;
    private final MenuService menuService;

    public AuthController(UserService userService,
                          MenuService menuService) {
        this.userService = userService;
        this.menuService = menuService;
    }

    @GetMapping("/login")
    public String loginForm(@RequestParam(name = "error", required = false) String error,
                            Model model) {
        if (error != null) {
            model.addAttribute("loginError", "아이디 또는 비밀번호가 올바르지 않습니다.");
        }
        return "login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String username,
                        @RequestParam String password,
                        HttpSession session) {
        User user = userService.login(username, password);
        session.setAttribute("loginUser", user);
        // 로그인 후 board/list 페이지로 이동
        return "redirect:/board/list";
    }

    @GetMapping("/register")
    public String registerForm() {
        return "register";
    }

    @PostMapping("/register")
    public String register(@RequestParam String username,
                           @RequestParam String password,
                           @RequestParam(defaultValue = "USER") String role) {
        userService.register(username, password, role);
        return "redirect:/login";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
}
