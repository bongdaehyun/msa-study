package com.sparta.msa_exam.order.orders;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class OrderRepositoryImpl implements OrderRepositoryCustom {
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Order findOrderAndItemById(Long orderId) {

        return jpaQueryFactory.selectFrom(QOrder.order)
                .leftJoin(QOrder.order.product_ids,QOrderItem.orderItem).fetchJoin()
                .where(
                        QOrder.order.orderId.eq(orderId)
                )
                .fetchOne();

    }
}
