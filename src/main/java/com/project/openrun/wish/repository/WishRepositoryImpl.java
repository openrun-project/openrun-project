package com.project.openrun.wish.repository;

import com.project.openrun.member.entity.Member;
import com.project.openrun.wish.dto.WishProductResponseDto;
import com.project.openrun.wish.entity.QWish;
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
import static com.project.openrun.wish.entity.QWish.wish;

@RequiredArgsConstructor
public class WishRepositoryImpl implements WishRepositoryCustom{
    private final JPAQueryFactory queryFactory;
    @Override
    public Page<WishProductResponseDto> findAllByMemberOrderByIdDesc(Member member, Pageable pageable) {
    List<WishProductResponseDto> content = queryFactory
            .select(Projections.constructor(WishProductResponseDto.class,
                    product.id,
                    product.productName,
                    product.price,
                    product.mallName,
                    product.productImage
                    ))
            .from(wish)
            .leftJoin(wish.product, product)
            .where(wish.member.eq(member))
            .orderBy(wish.id.desc())
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();


        JPAQuery<Long> countQuery = queryFactory
                .select(wish.count())
                .from(wish)
                .where(wish.member.eq(member));

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }



}
/*    Long id,
    String productName,
    Integer price,
    String mallName,
    String productImage*/

