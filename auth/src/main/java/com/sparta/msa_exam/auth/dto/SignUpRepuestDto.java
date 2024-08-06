package com.sparta.msa_exam.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;


@Data
@AllArgsConstructor
public class SignUpRepuestDto {

    private String username;
    private String email;
    private String password;
    private UserRole role;
}