package com.project.openrun.orders.repository;

import com.project.openrun.member.entity.Member;
import com.project.openrun.orders.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.domain.Pageable;
public interface OrderRepository extends JpaRepository<Order, Long> {

    Page<Order> findAllByMember(Member member, Pageable pageable);

}