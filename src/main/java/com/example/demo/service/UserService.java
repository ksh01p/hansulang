package com.example.demo.service;
import com.example.demo.domain.User;

/**
 * 5/2(금) 김수형
 * UserService는 사용자 관련 서비스 로직의 명세(인터페이스)를 정의합니다.
 *
 * 이 인터페이스는 "회원가입"과 "로그인" 기능을 제공하며,
 * 실제 구현은 UserServiceImpl 클래스에서 이루어집니다.
 *
 * --> 안전성을 위해서 반드시 service layer는 interface를 생성하는 것을 원칙으로 합니다 !!!!!
 */
public interface UserService {

    /**
     * 회원가입 기능
     * - 전달받은 username, password, role을 기반으로 새 사용자를 등록합니다.
     * - 구현체에서는 중복 사용자 검사와 저장 로직을 포함해야 합니다.
     *
     * @param username 사용자명
     * @param password 비밀번호
     * @param role 사용자 역할 (예: USER, ADMIN)
     */
    void register(String username, String password, String role);

    /**
     * 로그인 기능
     * - 사용자명과 비밀번호를 확인한 후, 성공 시 User 객체를 반환합니다.
     * - 실패 시 예외를 발생시킬 수 있습니다.
     *
     * @param username 사용자명
     * @param password 비밀번호
     * @return 인증된 사용자(User) 객체
     */
    User login(String username, String password);
}
