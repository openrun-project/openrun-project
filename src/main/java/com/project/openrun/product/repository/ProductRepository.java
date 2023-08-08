package com.project.openrun.product.repository;

import com.project.openrun.product.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long>,ProductRepositoryCustom  {


    @Query(value = "select * from product p where p.event_start_time >= now() order by p.wish_count desc limit :count", nativeQuery = true)
    List<Product> findTopCountProduct(@Param("count") Long count);
}
