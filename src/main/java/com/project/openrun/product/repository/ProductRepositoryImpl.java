package com.project.openrun.product.repository;

import com.project.openrun.product.dto.AllProductResponseDto;
import com.project.openrun.product.dto.OpenRunProductResponseDto;
import com.project.openrun.product.dto.ProductSearchCondition;
import com.project.openrun.product.entity.OpenRunStatus;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

import java.util.List;

import static com.project.openrun.product.entity.QProduct.product;
import static org.springframework.util.StringUtils.hasText;

@RequiredArgsConstructor
public class ProductRepositoryImpl implements  ProductRepositoryCustom{
    private final JPAQueryFactory queryFactory;


    @Override
    public Page<AllProductResponseDto> findAllDto(Pageable pageable,Long count) {
        List<AllProductResponseDto> content = queryFactory
                .select(Projections.constructor(AllProductResponseDto.class,
                        product.id,
                        product.productName,
                        product.price,
                        product.mallName,
                        product.category
                )).from(product)
                .orderBy(product.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();


        return new PageImpl<>(content, pageable, count);
    }


    @Override
    public Page<AllProductResponseDto> searchAllProducts(ProductSearchCondition condition, Pageable pageable){



        List<AllProductResponseDto> content = queryFactory
                .select(Projections.constructor(AllProductResponseDto.class,
                        product.id,
                        product.productName,
                        product.price,
                        product.mallName,
                        product.category

                )).from(product)
                .where(
                        keywordContains(condition.getKeyword()),
                        categoryEq(condition.getCategory()),
                        isOpenRunEq(condition.getStatus()),
                        isPriceBetween(condition.getLprice(), condition.getGprice())
                ).orderBy(sortMethod(condition.getSortBy(), condition.getIsAsc()))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> countQuery = queryFactory
                .select(product.count())
                .from(product)
                .where(
                        keywordContains(condition.getKeyword()),
                        categoryEq(condition.getCategory()),
                        isOpenRunEq(condition.getStatus()),
                        isPriceBetween(condition.getLprice(), condition.getGprice())
                );


        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }

    @Override
    public Page<OpenRunProductResponseDto> findOpenRunProducts(OpenRunStatus openRunStatus, Pageable pageable,Long count) {

        List<OpenRunProductResponseDto> content = queryFactory
                .select(Projections.constructor(OpenRunProductResponseDto.class,
                        product.id,
                        product.productName,
                        product.productImage,
                        product.price,
                        product.mallName,
                        product.category
                )).from(product)
                .where(product.status.eq(openRunStatus))
                .orderBy(product.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();


        return new PageImpl<>(content, pageable, count);
    }


    private OrderSpecifier<?> sortMethod(String sortBy, Boolean isAsc) {
        if(isAsc ==null){
            isAsc = true;
        }

        if(sortBy == null){
            return product.id.desc();
        }

        switch(sortBy){
            case "price" -> {
                return (isAsc == true) ? product.price.asc() : product.price.desc();
            }
            case "eventStartTime" -> {
                return (isAsc == true) ? product.eventStartTime.asc() : product.eventStartTime.desc();
            }
            case "wishCount" -> {
                return (isAsc == true) ? product.wishCount.asc() : product.wishCount.desc();
            }
            default -> {
                return product.id.desc();
            }
        }
    }

    private BooleanExpression keywordContains(String keyword) {
        return hasText(keyword) ? Expressions.booleanTemplate("match({0}) against ({1} in boolean mode)", product.productName, "+" + keyword + "*"): null;
//        return hasText(keyword) ? product.productName.contains(keyword).or(product.mallName.contains(keyword)) : null;

    }

    private BooleanExpression categoryEq(String category) {
        return hasText(category) ? product.category.eq(category) : null;
    }

    private BooleanExpression isOpenRunEq(OpenRunStatus status) {
        if( status == null){
            return null;
        }

        return hasText(status.toString()) ? product.status.eq(status) : null;
    }

    private BooleanExpression isPriceBetween(Integer lprice, Integer uprice) {

        if (lprice == null && uprice == null){
            return null;
        }else if(lprice == null){
            return product.price.loe(uprice);
        }else if(uprice == null){
            return product.price.goe(lprice);
        }

        return product.price.between(lprice, uprice);
    }
}
