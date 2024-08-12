package com.sparta.msa_exam.auth;

import com.sparta.msa_exam.auth.dto.AuthResponseDto;
import com.sparta.msa_exam.auth.dto.SignUpRepuestDto;
import com.sparta.msa_exam.auth.dto.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    @GetMapping("/signIn")
    public ResponseEntity<?> signIn(@RequestParam String username){
        String token = authService.createAccessToken(username,UserRole.normal);
        return ResponseEntity.ok(new AuthResponseDto(token));
    }

    @PostMapping("/signUp")
    public ResponseEntity<?> signUp(@RequestBody SignUpRepuestDto repuestDto){
        try {
            authService.signUp(repuestDto);
            return ResponseEntity.ok("회원가입이 성공적으로 완료되었습니다.");
        }catch(IllegalArgumentException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }

    }

    @GetMapping("/verify")
    public ResponseEntity<?> verify(@RequestParam String username){
        authService.verifyUser(username);
        return ResponseEntity.ok(true);
    }

}