package com.sparta.msa_exam.cart;

import com.sparta.msa_exam.cart.dto.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CartRepository extends JpaRepository<Cart,Long> {
}
