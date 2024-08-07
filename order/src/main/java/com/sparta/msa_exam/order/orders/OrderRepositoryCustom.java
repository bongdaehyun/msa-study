package com.sparta.msa_exam.order.orders;

public interface OrderRepositoryCustom {
    Order findOrderAndItemById(Long orderId);
}
