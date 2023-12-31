package com.example.demo.repository;

import com.example.demo.entity.RefreshToken;
import com.example.demo.entity.User;
import com.example.demo.exception.ResourceNotFoundException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
//
//@DataJpaTest
//@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
//public class RefreshTokenRepositoryTests {
//    @Autowired
//    private RefreshTokenRepository refreshTokenRepository;
//    @Autowired
//    UserRepository userRepository;
//
//    private RefreshToken refreshToken;
//    private User user;
//
//    @BeforeEach
//    void setup() {
//        user = User.builder()
//                .username("author")
//                .email("test@test.com")
//                .password("test")
//                .build();
//        userRepository.save(user);
//
//        refreshToken = RefreshToken.builder()
//                .token("token")
//                .user(user)
//                .build();
//        refreshTokenRepository.save(refreshToken);
//    }
//
//    @Test
//    void testSaveRefreshToken() {
//        RefreshToken newRefreshToken = RefreshToken.builder()
//                .token("1234test")
//                .build();
//        refreshTokenRepository.save(newRefreshToken);
//
//        RefreshToken retrievedRefreshToken = refreshTokenRepository.findById(newRefreshToken.getId()).get();
//
//        assertThat(retrievedRefreshToken).isNotNull();
//        assertThat(retrievedRefreshToken.getId()).isGreaterThan(0);
//        assertThat(retrievedRefreshToken.getToken()).isEqualTo("1234test");
//        assertThat(refreshTokenRepository.count()).isGreaterThan(0);
//    }
//
//    @Test
//    void testFindByToken_Success() {
//        RefreshToken retrievedRefreshToken = refreshTokenRepository.findByToken("token").get();
//
//        assertThat(retrievedRefreshToken).isNotNull();
//        assertThat(retrievedRefreshToken.getToken()).isEqualTo("token");
//    }
//
//    @Test
//    void testFindByToken_WhenRefreshTokenNotFound_ThrowsResourceNotFoundException() {
//        assertThrows(ResourceNotFoundException.class, () ->
//                refreshTokenRepository.findByToken("1234").orElseThrow(() ->
//                        new ResourceNotFoundException("Refresh Token not found")));
//    }
//
//    @Test
//    void testDeleteRefreshToken() {
//        long refreshTokensCount = refreshTokenRepository.count();
//
//        refreshTokenRepository.delete(refreshToken);
//
//        assertThat(refreshTokenRepository.count()).isEqualTo(refreshTokensCount - 1);
//        assertThat(refreshTokenRepository.findById(refreshToken.getId())).isEqualTo(Optional.empty());
//    }
//
//    @Test
//    void testDeleteRefreshTokenByUser() {
//        long refreshTokensCount = refreshTokenRepository.count();
//        System.out.println(refreshTokensCount);
//
//        refreshTokenRepository.deleteByUser(user);
//
//        assertThat(refreshTokenRepository.count()).isEqualTo(refreshTokensCount - 1);
//        assertThat(refreshTokenRepository.findById(refreshToken.getId())).isEqualTo(Optional.empty());
//    }
//}
