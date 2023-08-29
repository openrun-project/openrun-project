package com.project.openrun.orders.service;

import com.project.openrun.global.kafka.dto.OrderEventDto;
import com.project.openrun.global.kafka.producer.OrderCreateProducer;
import com.project.openrun.member.entity.Member;
import com.project.openrun.orders.dto.OrderRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderServiceFacade {

    private final OrderService orderService;
    private final OrderCreateProducer orderCreateProducer;

    public void CheckOrderPossibility(Long productId, OrderRequestDto orderRequestDto, Member member) {

        while (true) {
            try {
                OrderEventDto orderEventDto = orderService.postOrders(productId, orderRequestDto, member);
                orderCreateProducer.createOrder(orderEventDto);
                break;
            } catch (Exception e) {
                try {
                    Thread.sleep(50);
                } catch (InterruptedException ex) {
                    throw new RuntimeException(ex);
                }
            }
        }
    }

}
