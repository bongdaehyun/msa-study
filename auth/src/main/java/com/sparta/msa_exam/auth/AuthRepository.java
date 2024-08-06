package com.sparta.msa_exam.auth;

import com.sparta.msa_exam.auth.dto.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AuthRepository extends JpaRepository<User,Long>{
    Optional<User> findByUsername(String username);
}
