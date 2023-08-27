package com.project.openrun.product.repository;

import com.project.openrun.product.dto.AllProductResponseDto;
import com.project.openrun.product.dto.ProductSearchCondition;
import com.project.openrun.product.entity.OpenRunStatus;
import com.project.openrun.product.entity.Product;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.project.openrun.product.entity.QProduct.product;
import static org.springframework.util.StringUtils.hasText;

@Repository
@RequiredArgsConstructor
public class ProductsSearchRepository {


    private final EntityManager entityManager;

    private final JPAQueryFactory queryFactory;



    public Page<AllProductResponseDto> searchAllProductsUsingFullText(ProductSearchCondition condition, Pageable pageable) {
        List<Product> products;

        if (condition.getKeyword() != null && !condition.getKeyword().isEmpty()) {
            String baseQuery = "SELECT * FROM product WHERE MATCH(product_name) AGAINST (?1 IN BOOLEAN MODE)";
            StringBuilder queryBuilder = new StringBuilder(baseQuery);

            if (condition.getCategory() != null && !condition.getCategory().isEmpty()) {
                queryBuilder.append(" AND category = '"+ condition.getCategory() +"'");
            }
            if (condition.getStatus() != null) {
                queryBuilder.append(" AND status = '"+ condition.getStatus() +"'");
            }
            if (condition.getLprice() != null) {
                queryBuilder.append(" AND price >= " + condition.getLprice() );
            }
            if (condition.getGprice() != null) {
                queryBuilder.append(" AND price <= " + condition.getGprice());
            }
            queryBuilder.append(" ORDER BY " + getOrderBy(condition.getSortBy(), condition.getIsAsc()));
            queryBuilder.append(" LIMIT " + pageable.getPageSize() +  " OFFSET "+ pageable.getOffset() );

            Query query = entityManager.createNativeQuery(queryBuilder.toString(), Product.class);
            query.setParameter(1, "+" + condition.getKeyword() + "*");

            products = query.getResultList();
        } else {

            products = queryFactory
                    .selectFrom(product)
                    .where(
                            categoryEq(condition.getCategory()),
                            isOpenRunEq(condition.getStatus()),
                            isPriceBetween(condition.getLprice(), condition.getGprice())
                    )
                    .orderBy(sortMethod(condition.getSortBy(), condition.getIsAsc()))
                    .offset(pageable.getOffset())
                    .limit(pageable.getPageSize())
                    .fetch();
        }

        List<AllProductResponseDto> content = products.stream()
                .map(p -> new AllProductResponseDto(p.getId(), p.getProductName(), p.getPrice(), p.getMallName(), p.getCategory()))
                .collect(Collectors.toList());

        if (condition.getKeyword() != null && !condition.getKeyword().isEmpty()){
            return PageableExecutionUtils.getPage(content, pageable, () -> getCount(condition));
        }
        else
        {
            JPAQuery<Long> countQuery = queryFactory
                    .select(product.count())
                    .from(product)
                    .where(
                            categoryEq(condition.getCategory()),
                            isOpenRunEq(condition.getStatus()),
                            isPriceBetween(condition.getLprice(), condition.getGprice())
                    );


            return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
        }

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

    private String getOrderBy(String sortBy, Boolean isAsc) {
        String orderColumn;
        if(Objects.isNull(sortBy)){
            orderColumn = "product_id";
        }else{

            switch (sortBy) {
                case "price":
                    orderColumn = "price";
                    break;
                case "eventStartTime":
                    orderColumn = "event_start_time";
                    break;
                case "wishCount":
                    orderColumn = "wish_count";
                    break;
                default:
                    orderColumn = "product_id";
                    break;
            }
        }



        return orderColumn + (Boolean.TRUE.equals(isAsc) ? " ASC" : " DESC");
    }

    private long getCount(ProductSearchCondition condition) {

        String baseQuery = "SELECT COUNT(*) FROM product WHERE MATCH(product_name) AGAINST (?1 IN BOOLEAN MODE)";
        StringBuilder queryBuilder = new StringBuilder(baseQuery);


        if (condition.getCategory() != null && !condition.getCategory().isEmpty()) {
            queryBuilder.append(" AND category = '"+ condition.getCategory() +"'");
        }
        if (condition.getStatus() != null) {
            queryBuilder.append(" AND status = '"+ condition.getStatus() + "'");
        }

        if (condition.getLprice() != null) {
            queryBuilder.append(" AND price >= " + condition.getLprice() );
        }
        if (condition.getGprice() != null) {
            queryBuilder.append(" AND price <= " + condition.getGprice());
        }

        Query countQuery = entityManager.createNativeQuery(queryBuilder.toString());

        countQuery.setParameter(1, "+" + condition.getKeyword() + "*");


        Long result = (Long) countQuery.getSingleResult();

        return result;
    }

}

