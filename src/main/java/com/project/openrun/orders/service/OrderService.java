package com.project.openrun.orders.service;

import com.project.openrun.global.exception.OrderException;
import com.project.openrun.global.exception.type.OrderErrorCode;
import com.project.openrun.member.entity.Member;
import com.project.openrun.orders.dto.OrderRequestDto;
import com.project.openrun.orders.dto.OrderResponseDto;
import com.project.openrun.orders.entity.Order;
import com.project.openrun.orders.repository.OrderRepository;
import com.project.openrun.product.entity.Product;
import com.project.openrun.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    public List<OrderResponseDto> getOrders(Member member) {
//        Sort.Direction direction = Sort.Direction.DESC;
//        Sort sort = Sort.by(direction, "createdAt");
//
//        Pageable pageable = PageRequest.of(page, limit, sort);
//        Page<Tweets> retweets = reTweetsRepository.findAllByRetweets_Id(tweetId, pageable);

        List<Order> orders = orderRepository.findAllByMember(member).orElseThrow(
                () -> new OrderException(OrderErrorCode.NOT_ORDER)
        );

        return orders.map(order -> {
            return new OrderResponseDto(
                    order.getId(),
                    order.getProduct().getProductName(),
                    order.getProduct().getPrice(),
                    order.getProduct().getMallName(),
                    order.getCount()
            );
        }).collect(Collectors.toList());
    }

    @Transactional
    public void postOrders(Long productId, OrderRequestDto orderRequestDto, Member member) {

        Product product = productRepository.findById(productId).orElseThrow(
                () -> new OrderException(OrderErrorCode.NOT_PRODUCT)
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

    @Transactional
    public void deleteOrders(Long orderId, Member member) {
        Order order = orderRepository.findById(orderId).orElseThrow(
                () -> new OrderException(OrderErrorCode.NOT_ORDER)
        );

        if (order.getMember().getId() != member.getId()) {
            throw new OrderException(OrderErrorCode.NOT_USER_ORDER);

        }

        order.getProduct().increaseQuantity(order.getCount());

        orderRepository.delete(order);
    }
}
