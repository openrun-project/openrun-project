package com.project.openrun.global.kafka.dto;

import com.project.openrun.member.entity.Member;
import com.project.openrun.orders.dto.OrderRequestDto;
import com.project.openrun.product.entity.Product;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class OrderEventDto {

    private Product product;
    private OrderRequestDto orderRequestDto;
    private Member member;

}
