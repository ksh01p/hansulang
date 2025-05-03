package com.example.demo.repository;

import com.example.demo.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 *
 * 5/2(금) 김수형
 *
 * UserRepository는 사용자(User) 엔티티에 대한 DB 작업을 처리하는 인터페이스입니다.
 * Spring Data JPA가 내부적으로 구현체를 자동 생성해줍니다.
 *
 * JpaRepository<User, Long>
 * - User: 엔티티 클래스
 * - Long: User 엔티티의 기본 키(PK) 타입 (id 필드가 Long이므로)
 */
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * 사용자명(username)으로 User를 조회하는 메서드입니다.
     * - 메서드 이름을 기반으로 Spring Data JPA가 자동으로 SQL을 생성합니다.
     * - 예: SELECT * FROM user WHERE username = ?
     *
     * @param username 조회할 사용자 이름
     * @return Optional<User>: 결과가 없을 수도 있으므로 Optional로 반환
     */
    Optional<User> findByUsername(String username);
}
