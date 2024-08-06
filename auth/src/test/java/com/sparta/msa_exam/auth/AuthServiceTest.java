package com.sparta.msa_exam.auth;

import com.sparta.msa_exam.auth.dto.SignUpRepuestDto;
import com.sparta.msa_exam.auth.dto.User;
import com.sparta.msa_exam.auth.dto.UserRole;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import io.jsonwebtoken.Jwts;

import javax.crypto.SecretKey;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private AuthRepository authRepository;

    private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    private String issuer = "auth-service" ;

    private Long accesExpiration = 3600000L;

    private String secretKey ="401b09eab3c013d4ca54922bb802bec8fd5318192b0a75f201d8b3727429080fb337591abd3e44453b954555b7a0812e1081c39b740293f765eae731f5a65ed1";

    private AuthService authService;

    @BeforeEach
    void setUp() {
        authService = new AuthService(secretKey,accesExpiration,issuer,authRepository
       ,passwordEncoder);
    }

    @Test
    @DisplayName("jwt토큰 생성테스트")
    void jwtTest() {

        //given
        String username = "bong";
        UserRole role = UserRole.normal;

        //when
        String token = authService.createAccessToken(username,role);

        //then
        SecretKey key = Keys.hmacShaKeyFor(Decoders.BASE64URL.decode(secretKey));
        Jws<Claims> claimsJws = Jwts.parser()
                .verifyWith(key)
                .build().parseClaimsJws(token);

        Claims claims = claimsJws.getBody();

        assertEquals(username, claims.get("username"));
        assertEquals(role.toString(), claims.get("role"));
        assertEquals(issuer, claims.getIssuer());
        assertNotNull(claims.getIssuedAt());
        assertNotNull(claims.getExpiration());
    }

    @Test
    @DisplayName("회원가입 Erorr 테스트")
    void signInErrorTest(){
        //given
        SignUpRepuestDto requestDto =
                new SignUpRepuestDto("test","test@test.com","1234",UserRole.normal);

        given(authRepository.findByUsername(requestDto.getUsername())).willReturn(Optional.of(new User()));
        //when
        Exception thrown = assertThrows(IllegalArgumentException.class, () -> {
            authService.signUp(requestDto);
        });

        //then
        assertEquals("이미 등록된 회원입니다", thrown.getMessage());
    }
}