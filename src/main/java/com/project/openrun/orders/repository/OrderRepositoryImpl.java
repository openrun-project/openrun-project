package com.project.openrun.orders.repository;

import com.project.openrun.member.entity.Member;
import com.project.openrun.orders.dto.OrderResponseDto;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

import java.util.List;

import static com.project.openrun.orders.entity.QOrder.order;
import static com.project.openrun.product.entity.QProduct.product;

@RequiredArgsConstructor
public class OrderRepositoryImpl implements OrderRepositoryCustom{
    private final JPAQueryFactory queryFactory;

    @Override
    public Page<OrderResponseDto> findAllByMember(Member member, Pageable pageable) {
        List<OrderResponseDto> content = queryFactory
                .select(Projections.constructor(OrderResponseDto.class,
                        order.id,
                        product.productName,
                        product.price,
                        product.mallName,
                        order.count,
                        order.modifiedAt
                ))
                .from(order)
                .leftJoin(order.product, product)//left조인과 Projections 를 사용해 fetchJoin을 미사용
                .where(order.member.eq(member))
                .orderBy(order.modifiedAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();


        JPAQuery<Long> countQuery = queryFactory
                .select(order.id.count())
                .from(order)
                .where(order.member.eq(member));

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);

    }
}
