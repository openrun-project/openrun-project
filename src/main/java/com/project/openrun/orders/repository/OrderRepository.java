package com.project.openrun.orders.repository;

import com.project.openrun.orders.entity.Order;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;


public interface OrderRepository extends JpaRepository<Order, Long>, OrderRepositoryCustom {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select o from Order o join fetch o.product where o.id = :id")
    Optional<Order> findWithLockById(@Param("id") Long id);
}