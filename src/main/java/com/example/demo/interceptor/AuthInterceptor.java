package com.example.demo.interceptor;

import com.example.demo.domain.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 5/2(금) 김수형
 * AuthInterceptor는 사용자의 로그인 여부와 관리자 권한을 검사하는 인터셉터
 * 이 클래스는 컨트롤러가 실행되기 전에 요청을 가로채어 검사하는 역할을 합니다.
 */
@Component
public class AuthInterceptor implements HandlerInterceptor {

    /**
     * preHandle 메서드는 컨트롤러 메서드가 실행되기 전에 호출됩니다.
     *
     * 1. 모든 요청에서 세션에 loginUser가 존재하는지 확인합니다. (로그인 여부 검사)
     * 2. 요청 URI가 /admin으로 시작하는 경우, 로그인된 사용자가 관리자(ADMIN)인지 확인합니다.
     *
     * @return true → 요청 계속 진행
     *         false → 컨트롤러 진입 막고 redirect 처리
     */
    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {

        // 현재 요청에 대한 세션 가져오기 (false: 세션이 없으면 null 반환)
        HttpSession session = request.getSession(false);

        // [1] 로그인 여부 검사: 세션이 없거나 loginUser 속성이 없다면 로그인 페이지로 이동
        if (session == null || session.getAttribute("loginUser") == null) {
            response.sendRedirect("/login");  // 로그인 페이지로 리다이렉트
            return false; // 더 이상 요청 처리하지 않음
        }

        // [2] 관리자 페이지 접근 시 권한 검사
        // 세션에서 로그인된 사용자 정보 가져오기
        User user = (User) session.getAttribute("loginUser");

        // 요청 URI 확인
        String uri = request.getRequestURI();

        // /admin 으로 시작하는 URI에 대해 ADMIN 권한이 아닌 경우 접근 차단
        if (uri.startsWith("/admin") && !"ADMIN".equals(user.getRole())) {
            response.sendRedirect("/access-denied"); // 접근 거부 페이지로 이동
            return false;
        }

        // 로그인 되어 있고, 권한도 충분하다면 요청을 계속 진행
        return true;
    }
}
