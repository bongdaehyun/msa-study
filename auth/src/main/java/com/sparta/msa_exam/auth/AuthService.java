package com.sparta.msa_exam.auth;

import com.sparta.msa_exam.auth.dto.User;
import com.sparta.msa_exam.auth.dto.UserRole;
import com.sparta.msa_exam.auth.dto.SignInRequestDto;
import com.sparta.msa_exam.auth.dto.SignUpRepuestDto;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;


@Service
public class AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);
    private final AuthRepository authRepository;

    private final PasswordEncoder passwordEncoder;


    private String issuer;

    private Long accesExpiration;

    private final SecretKey secretKey;

    public AuthService(@Value("${service.jwt.secret-key}") String secretKey,
                       @Value("${service.jwt.access-expiration}") Long accesExpiration,
                       @Value("${spring.application.name}") String issuer,
                       AuthRepository authRepository
            , PasswordEncoder passwordEncoder) {
        this.secretKey = Keys.hmacShaKeyFor(Decoders.BASE64URL.decode(secretKey));
        this.accesExpiration = accesExpiration;
        this.issuer = issuer;
        this.authRepository = authRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public void signUp(SignUpRepuestDto requestDto)
    {
        log.info("signUp requestDto:{}", requestDto);
        //이미 회원인지 여부 판단
        authRepository.findByUsername(requestDto.getUsername()).ifPresent(
            user -> { throw new IllegalArgumentException("이미 등록된 회원입니다");}
        );

        String password = passwordEncoder.encode(requestDto.getPassword());

        User user = User.builder()
                .username(requestDto.getUsername())
                .password(password)
                .email(requestDto.getEmail())
                .role(requestDto.getRole())
                .build();

        authRepository.save(user);
    }

    public String signIn(SignInRequestDto requestDto)
    {
        User user = authRepository.findByUsername(requestDto.getUsername()).orElseThrow(
                ()->  new IllegalArgumentException("잘못된 회원이름입니다.")
        );

        if(!passwordEncoder.matches(requestDto.getPassword(),user.getPassword()))
            throw new IllegalArgumentException("잘못된 비밀번호입니다.");

        return createAccessToken(user.getUsername(),user.getRole());
    }

    public String createAccessToken(String username, UserRole role) {
        return Jwts.builder()
                .claim("username",username)
                .claim("role", role)
                .issuer(issuer)
                .issuedAt(new Date((System.currentTimeMillis())))
                .expiration(new Date(System.currentTimeMillis() + accesExpiration))
                .signWith(secretKey, SignatureAlgorithm.HS512)
                .compact();
    }

}
