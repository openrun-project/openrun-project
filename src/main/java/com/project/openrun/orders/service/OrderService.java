package com.project.openrun.orders.service;

import com.project.openrun.global.kafka.dto.OrderEventDto;
import com.project.openrun.global.kafka.producer.OrderCreateProducer;
import com.project.openrun.member.entity.Member;
import com.project.openrun.orders.dto.OrderRequestDto;
import com.project.openrun.orders.dto.OrderResponseDto;
import com.project.openrun.orders.entity.Order;
import com.project.openrun.orders.repository.OrderRepository;
import com.project.openrun.product.repository.OpenRunProductRedisRepositoryImpl;
import com.project.openrun.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Objects;

import static com.project.openrun.global.exception.type.ErrorCode.NOT_AUTHORIZATION;
import static com.project.openrun.global.exception.type.ErrorCode.NOT_FOUND_DATA;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final OrderCreateProducer orderCreateProducer;
    private final OpenRunProductRedisRepositoryImpl openRunProductRedisRepository;


    @Transactional(readOnly = true)
    public Page<OrderResponseDto> getOrders(Member member, Pageable pageable) {

        Page<OrderResponseDto> order = orderRepository.findAllByMember(member, pageable);
        if (order.isEmpty()) {
            throw new ResponseStatusException(NOT_FOUND_DATA.getStatus(), NOT_FOUND_DATA.formatMessage("주문"));
        }

        return order;

    }


    public void postOrders(Long productId, OrderRequestDto orderRequestDto, Member member) {

        if (openRunProductRedisRepository.decreaseQuantity(productId, orderRequestDto.count()) < 0) {

            openRunProductRedisRepository.increaseQuantity(productId, orderRequestDto.count());
            throw new ResponseStatusException(NOT_FOUND_DATA.getStatus(), NOT_FOUND_DATA.formatMessage("재고 부족"));
        }

        OrderEventDto orderEventDto = new OrderEventDto(productId, orderRequestDto, member);

        orderCreateProducer.createOrder(orderEventDto);

    }

    @Transactional
    public void deleteOrders(Long orderId, Member member) throws ResponseStatusException {
        Order order = orderRepository.findWithLockById(orderId).orElseThrow(
                () -> new ResponseStatusException(NOT_FOUND_DATA.getStatus(), NOT_FOUND_DATA.formatMessage("주문"))
        );

        if (!Objects.equals(order.getMember().getId(), member.getId())) {
            throw new ResponseStatusException(NOT_AUTHORIZATION.getStatus(), NOT_AUTHORIZATION.formatMessage("주문"));
        }

        productRepository.updateProductQuantity(order.getCount(), order.getProduct().getId());

        openRunProductRedisRepository.increaseQuantity(order.getProduct().getId(), order.getCount());

        orderRepository.delete(order);
    }
}
