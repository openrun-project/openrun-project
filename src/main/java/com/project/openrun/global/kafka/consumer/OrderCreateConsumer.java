package com.project.openrun.global.kafka.consumer;

import com.project.openrun.global.kafka.producer.dto.OrderEventDto;
import com.project.openrun.member.entity.Member;
import com.project.openrun.orders.entity.Order;
import com.project.openrun.orders.repository.OrderRepository;
import com.project.openrun.product.entity.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderCreateConsumer{

    private final OrderRepository orderRepository;

    @KafkaListener(topics = "orders"/*, groupId = "${kafka.group-id}", containerFactory = "kafkaListenerContainerFactory"*/)
    public void consumerOrderCreate(OrderEventDto orderEventDto){

//        Product product = orderEventDto.getProduct();
//        Member member = orderEventDto.getMember();
//        Integer count = orderEventDto.getOrderRequestDto().count();
//
//        //kafka 로 미룰 예정
//        Order order = Order.builder()
//                .member(member)
//                .product(product)
//                .count(count)
//                .totalPrice(product.getPrice() * count)
//                .build();
//        
//        orderRepository.save(order);

        System.out.println("orderEventDto.getProduct().getId() = " + orderEventDto.getProduct().getId());
    }


}


