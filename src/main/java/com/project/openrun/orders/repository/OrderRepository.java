package com.project.openrun.orders.repository;

import com.project.openrun.member.entity.Member;
import com.project.openrun.orders.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

    Optional<List<Order>> findAllByMember(Member member);
}