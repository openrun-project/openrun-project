package com.project.openrun.orders.repository;

import com.project.openrun.member.entity.Member;
import com.project.openrun.orders.dto.OrderResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OrderRepositoryCustom {

    Page<OrderResponseDto> findAllByMember(Member member, Pageable pageable);
}
