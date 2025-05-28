package com.example.demo.service.impl;

import com.example.demo.domain.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.UserService;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    // 생성자 주입
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * 회원가입 처리
     * 1. 이메일 형식이 올바른지 확인
     * 2. 이메일 도메인이 @handong.ac.kr 인지 확인
     * 3. 중복 사용자 여부 확인
     * 4. 문제 없으면 저장
     */
    @Override
    public void register(String username, String password, String role) {
        // 1. 이메일 형식 검사
        if (!username.matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$")) {
            throw new IllegalArgumentException("올바른 이메일 형식이 아닙니다.");
        }

        // 2. 이메일 도메인 검사
        if (!username.endsWith("@handong.ac.kr")) {
            throw new IllegalArgumentException("이메일은 @handong.ac.kr로 끝나야 합니다");
        }

        // 3. 중복 사용자 검사
        userRepository.findByUsername(username).ifPresent(u -> {
            throw new IllegalArgumentException("이미 존재하는 사용자입니다.");
        });

        // 4. 저장
        User user = new User();
        user.setUsername(username);
        user.setPassword(password); // ❗ 실제 환경에서는 BCrypt로 암호화 필요
        user.setRole(role);

        userRepository.save(user);
    }

    /**
     * 로그인 처리
     * 1. 사용자 조회
     * 2. 비밀번호 일치 여부 확인
     */
    @Override
    public User login(String username, String password) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("아이디 또는 비밀번호가 올바르지 않습니다."));

        if (!user.getPassword().equals(password)) {
            throw new IllegalArgumentException("아이디 또는 비밀번호가 올바르지 않습니다.");
        }

        return user;
    }
}