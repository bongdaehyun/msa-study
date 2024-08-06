package com.sparta.msa_exam.auth.dto;

import lombok.Data;
import lombok.Getter;

@Data
public class SignInRequestDto {
    private String username;
    private String password;
}