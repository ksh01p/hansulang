package com.example.demo.config;

import com.example.demo.interceptor.AuthInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 5/2(금) 김수형
 * Spring WebConfig 설정 파일
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    // 인증 관련 요청을 가로채는 인터셉터를 의존성 주입받습니다.
    private final AuthInterceptor authInterceptor;

    // 생성자를 통해 AuthInterceptor를 주입합니다.
    public WebConfig(AuthInterceptor authInterceptor) {
        this.authInterceptor = authInterceptor;
    }

    // 이 메서드는 인터셉터를 등록하는 역할을 합니다.
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authInterceptor) // AuthInterceptor를 등록하고,
                .addPathPatterns("/**")          // 모든 경로에 대해 적용합니다.
                .excludePathPatterns(           // 아래의 경로들은 예외로 설정합니다.
                        "/login", "/login/**",           // 로그인 페이지 및 관련 요청
                        "/register", "/register/**",     // 회원가입 페이지 및 관련 요청
                        "/api/auth/**",                  // REST 방식의 인증 요청 (로그인, 회원가입, 로그아웃 등)
                        "/css/**", "/js/**", "/images/**", // 정적 자원 (CSS, JavaScript, 이미지 등)
                        "/favicon.ico",                  // 브라우저 탭에 표시되는 파비콘 요청
                        "/error"                         // Spring 기본 에러 처리 경로
                );
    }
}
