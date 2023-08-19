package com.project.openrun.orders.service;

import com.project.openrun.member.entity.Member;
import com.project.openrun.orders.dto.OrderRequestDto;
import com.project.openrun.orders.dto.OrderResponseDto;
import com.project.openrun.orders.entity.Order;
import com.project.openrun.orders.repository.OrderRepository;
import com.project.openrun.product.entity.Product;
import com.project.openrun.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static com.project.openrun.global.exception.type.ErrorCode.NOT_AUTHORIZATION;
import static com.project.openrun.global.exception.type.ErrorCode.NOT_FOUND_DATA;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;


    // fetchJoin 이후에 적용 -> ok
    public Page<OrderResponseDto> getOrders(Member member, Pageable pageable) {

        Page<OrderResponseDto> order = orderRepository.findAllByMember(member, pageable);
        if (order.isEmpty()) {
            throw new ResponseStatusException(NOT_FOUND_DATA.getStatus(), NOT_FOUND_DATA.formatMessage("주문"));
        }

        return order;

    }


    @Transactional
    public void postOrders(Long productId, OrderRequestDto orderRequestDto, Member member) {

        Product product = productRepository.findWithLockById(productId).orElseThrow(
                () -> new ResponseStatusException(NOT_FOUND_DATA.getStatus(), NOT_FOUND_DATA.formatMessage("상품"))
        );

        Order order = Order.builder()
                .member(member)
                .product(product)
                .count(orderRequestDto.count())
                .totalPrice(product.getPrice() * orderRequestDto.count())
                .build();

        product.decreaseQuantity(orderRequestDto.count());

        orderRepository.save(order);
    }

    // fetchJoin 이후에 적용
    @Transactional
    public void deleteOrders(Long orderId, Member member) throws ResponseStatusException {
        Order order = orderRepository.findWithLockById(orderId).orElseThrow(
                () -> new ResponseStatusException(NOT_FOUND_DATA.getStatus(), NOT_FOUND_DATA.formatMessage("주문")
        ));

        if (order.getMember().getId() != member.getId()) {
            throw new ResponseStatusException(NOT_AUTHORIZATION.getStatus(), NOT_AUTHORIZATION.formatMessage("주문"));
        }

        order.getProduct().increaseQuantity(order.getCount());

        orderRepository.delete(order);
    }
}
