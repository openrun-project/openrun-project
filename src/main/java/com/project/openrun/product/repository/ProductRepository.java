package com.project.openrun.product.repository;

import com.project.openrun.product.entity.OpenRunStatus;
import com.project.openrun.product.entity.Product;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long>,ProductRepositoryCustom  {


    @Query(value = "select * from product p where p.event_start_time >= now() order by p.wish_count desc limit :count", nativeQuery = true)
    List<Product> findTopCountProduct(@Param("count") Long count);

    Page<Product> findAllByStatusOrderByWishCountDescProductNameDesc(OpenRunStatus openRunStatus, Pageable pageable);


    @Modifying(flushAutomatically = true)
    @Query(value = "UPDATE Product p SET p.status = :openRunStatus WHERE p.eventStartTime between :yesterday AND :today AND p.status = :nowStatus")
    int updateProductStatus(@Param("yesterday")LocalDateTime yesterday, @Param("today")LocalDateTime today, @Param("openRunStatus") OpenRunStatus openRunStatus, @Param("nowStatus") OpenRunStatus nowStatus);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
//    @QueryHints({@QueryHint(name = "jakarta.persistence.lock.timeout", value ="50")})
    Optional<Product> findWithLockById(Long productId);

    Long countByStatus(OpenRunStatus status);

    @Transactional
    @Modifying(flushAutomatically = true)
    @Query(value = "UPDATE Product p SET p.currentQuantity = p.currentQuantity + :count where p.id = :id")
    int updateProductQuantity(@Param("count") Integer count, @Param("id") Long id);


}
