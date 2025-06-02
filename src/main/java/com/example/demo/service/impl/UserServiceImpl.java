package com.example.demo.service.impl;

import com.example.demo.domain.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.UserService;
import org.springframework.stereotype.Service;

/**
 * 5/2(금) 김수형
 *
 * #### 실제 서비스에서는 반드시 BCryptPasswordEncoder를 사용해 암호화/검증을 처리해야 합니다. ####
 * --> 곧 구현할 예정 ! 아마 구현 안 하지 않을까 싶은데.. 그래도 한 번 속도 봐서 이 정도까지는 해볼까 합니다 !
 *
 *
 * UserServiceImpl은 UserService 인터페이스를 구현한 Service
 * 사용자 회원가입, 로그인 등의 핵심 비즈니스 로직을 처리합니다.
 */
@Service
public class UserServiceImpl implements UserService {

    // 사용자 정보를 DB에서 처리하기 위한 레포지토리
    private final UserRepository userRepository;

    // 생성자 주입 (의존성 주입) 방식으로 UserRepository를 전달받음
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * 회원가입 처리 메서드
     *
     * 1. username 중복 여부를 검사합니다.
     * 2. 중복된 사용자가 없으면 새 User 객체를 생성하여 저장합니다.
     *    - 현재는 비밀번호를 평문(암호화 없이)으로 저장하고 있음 (실제 서비스에서는 보안상 위험!)
     *
     * @param username 사용자명
     * @param password 비밀번호
     * @param role 사용자 권한 (예: USER, ADMIN)
     */
    @Override
    public void register(String username, String password, String role) {
        // 이미 존재하는 사용자명이면 예외 발생
        userRepository.findByUsername(username).ifPresent(u -> {
            throw new IllegalArgumentException("이미 존재하는 사용자입니다.");
        });

        // 사용자 객체 생성 후 값 설정
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);  // ❗❗ 평문 비밀번호 저장 (보안상 비추 → BCrypt 사용 권장)
        user.setRole(role);

        // DB에 저장
        userRepository.save(user);
    }

    /**
     * 로그인 처리 메서드
     *
     * 1. username으로 사용자 정보를 조회
     * 2. 입력한 password와 DB에 저장된 password가 일치하는지 검사
     * 3. 둘 다 만족하면 User 객체 반환
     *
     * @param username 사용자명
     * @param password 비밀번호
     * @return 로그인 성공한 User 객체
     */
    @Override
    public User login(String username, String password) {
        // 사용자명으로 사용자 조회 (없으면 예외)
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("아이디 또는 비밀번호가 올바르지 않습니다."));

        // 비밀번호 일치 여부 검사 (현재는 평문 비교)
        if (!user.getPassword().equals(password)) {
            throw new IllegalArgumentException("아이디 또는 비밀번호가 올바르지 않습니다.");
        }

        // 로그인 성공 시 사용자 객체 반환
        return user;
    }
}
