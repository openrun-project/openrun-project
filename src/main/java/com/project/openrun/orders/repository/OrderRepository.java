package com.project.openrun.orders.repository;

import com.project.openrun.member.entity.Member;
import com.project.openrun.orders.dto.OrderResponseDto;
import com.project.openrun.orders.entity.Order;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


public interface OrderRepository extends JpaRepository<Order, Long>, OrderRepositoryCustom {
//    @Query("SELECT new com.project.openrun.orders.dto.OrderResponseDto(o.id, p.productName, p.price, p.mallName, o.count, o.modifiedAt) " +
//            "FROM Order o " +
//            "LEFT JOIN FETCH o.product p " +
//            "WHERE o.member = :member " +
//            "ORDER BY o.modifiedAt DESC")
//    List<OrderResponseDto> findAllByMember(@Param("member") Member member, Pageable pageable);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select o from Order o join fetch o.product where o.id = :id")
    Optional<Order> findWithLockById(@Param("id")Long id);
}