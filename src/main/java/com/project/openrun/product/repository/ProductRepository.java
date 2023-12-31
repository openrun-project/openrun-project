package com.project.openrun.product.repository;

import com.project.openrun.product.entity.OpenRunStatus;
import com.project.openrun.product.entity.Product;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long>,ProductRepositoryCustom  {


    @Query(value = "select * from product p " +
            "where p.event_start_time > DATE(now()) " +
            "and p.event_start_time < DATE(DATE_ADD(now(), INTERVAL 1 DAY )) " +
            "order by p.wish_count desc limit :count", nativeQuery = true)
    List<Product> findTopCountProduct(@Param("count") Long count);


    @Modifying(flushAutomatically = true)
    @Query(value = "UPDATE Product p SET p.status = :openRunStatus WHERE p.eventStartTime between :yesterday AND :today AND p.status = :nowStatus")
    int updateProductStatus(@Param("yesterday")LocalDateTime yesterday, @Param("today")LocalDateTime today, @Param("openRunStatus") OpenRunStatus openRunStatus, @Param("nowStatus") OpenRunStatus nowStatus);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Product> findWithLockById(Long productId);

    Long countByStatus(OpenRunStatus status);

}
