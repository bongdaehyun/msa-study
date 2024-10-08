package com.sparta.msa_exam.gateway;

import com.sparta.msa_exam.gateway.client.AuthService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import javax.crypto.SecretKey;

@Slf4j
@Component
public class JwtUtil {

    @Value("${service.jwt.secret-key}")
    private String secretKey;

    private final AuthService authService;

    public JwtUtil(@Lazy AuthService authService) {
        this.authService = authService;
    }

    public String extractToken(ServerWebExchange exchange) {
        String authHeader = exchange.getRequest().getHeaders().getFirst("Authorization");

        if(authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }

        return null;
    }

    public boolean validateToken(String token, ServerWebExchange exchange)
    {
        log.info("Validating token");
        try {
            log.info("secretKey :" +secretKey);
            SecretKey key = Keys.hmacShaKeyFor(Decoders.BASE64URL.decode(secretKey));
            Jws<Claims> claimsJws = Jwts.parser()
                    .verifyWith(key)
                    .build().parseClaimsJws(token);
            log.info("#####payload :: " + claimsJws.getPayload().toString());
            //회원존재 검증
            if(claimsJws.getPayload().get("username") == null)
            {
                return false;
            }

            //로그인한 뒤 사용할 아이디 및 역할 저장
            Claims claims = claimsJws.getBody();
            String username = claims.get("username").toString();

            boolean result = authService.verifyUser(username);
            if(result == false) return false;

            exchange.getRequest().mutate()
                    .header("X-USER-NAME", username )
                    .header("X-ROLE", claims.get("role").toString())
                    .build();

            return true;
        }catch (Exception e) {
            log.warn(e.getMessage());
            return false;
        }
    }
}
