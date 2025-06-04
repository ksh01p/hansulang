package com.example.demo.service.impl;

import com.example.demo.domain.User;
import com.example.demo.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class UserServiceImplTest {

    @Autowired
    private UserServiceImpl userService;

    @MockBean
    private UserRepository userRepository;

    @Value("${test-user.password}")
    private String testPassword;

    @Test
    void testDuplicateEmail_ThrowsException() {
        String username = "test@handong.ac.kr";
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(new User()));

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> userService.register(username, testPassword, "USER")
        );

        assertEquals("이미 존재하는 사용자입니다.", exception.getMessage());
    }

    @Test
    void testSuccessfulRegistration_SavesUser() {
        String username = "new@handong.ac.kr";
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        userService.register(username, testPassword, "USER");

        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testLoginWithWrongUsername_ThrowsException() {
        when(userRepository.findByUsername("unknown@handong.ac.kr")).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> userService.login("unknown@handong.ac.kr", testPassword)
        );

        assertEquals("아이디 또는 비밀번호가 올바르지 않습니다.", exception.getMessage());
    }

    @Test
    void testLoginWithWrongPassword_ThrowsException() {
        User user = new User();
        user.setUsername("valid@handong.ac.kr");
        user.setPassword("correctpw");

        when(userRepository.findByUsername("valid@handong.ac.kr"))
                .thenReturn(Optional.of(user));

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> userService.login("valid@handong.ac.kr", testPassword) // 잘못된 비밀번호
        );

        assertEquals("아이디 또는 비밀번호가 올바르지 않습니다.", exception.getMessage());
    }

    @Test
    void testLoginSuccess_ReturnsUser() {
        User user = new User();
        user.setUsername("valid@handong.ac.kr");
        user.setPassword(testPassword); // 올바른 비밀번호

        when(userRepository.findByUsername("valid@handong.ac.kr"))
                .thenReturn(Optional.of(user));

        User result = userService.login("valid@handong.ac.kr", testPassword);

        assertNotNull(result);
        assertEquals("valid@handong.ac.kr", result.getUsername());
    }

    @Test
    void testInvalidEmailFormat_ThrowsException() {
        String[] invalidEmails = {
                "abc", "hello@", "no-domain@", "user@com", "user@.com", "user@domain.", "@handong.ac.kr"
        };

        for (String email : invalidEmails) {
            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> userService.register(email, testPassword, "USER"),
                    "예외 없이 통과된 이메일: " + email
            );

            assertEquals("올바른 이메일 형식이 아닙니다.", exception.getMessage());
        }
    }

    @Test
    void testNonHandongEmailDomain_ThrowsException() {
        String[] nonHandongEmails = {
                "user@gmail.com", "user@naver.com", "user@kakao.com", "user@hanyang.ac.kr"
        };

        for (String email : nonHandongEmails) {
            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> userService.register(email, testPassword, "USER"),
                    "예외 없이 통과된 이메일: " + email
            );

            assertEquals("이메일은 @handong.ac.kr로 끝나야 합니다", exception.getMessage());
        }
    }
}
