package com.project.openrun.orders.service;

import com.project.openrun.global.util.RedisLock;
import com.project.openrun.member.entity.Member;
import com.project.openrun.orders.dto.OrderRequestDto;
import com.project.openrun.orders.dto.OrderResponseDto;
import com.project.openrun.orders.entity.Order;
import com.project.openrun.orders.repository.OrderRepository;
import com.project.openrun.global.kafka.producer.OrderCreateProducer;
import com.project.openrun.global.kafka.dto.OrderEventDto;
import com.project.openrun.product.entity.OpenRunStatus;
import com.project.openrun.product.entity.Product;
import com.project.openrun.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import static com.project.openrun.global.exception.type.ErrorCode.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final OrderCreateProducer orderCreateProducer;
    private final RedisLock redisLock;


    // fetchJoin 이후에 적용 -> ok
    public Page<OrderResponseDto> getOrders(Member member, Pageable pageable) {

        Page<OrderResponseDto> order = orderRepository.findAllByMember(member, pageable);
        if (order.isEmpty()) {
            throw new ResponseStatusException(NOT_FOUND_DATA.getStatus(), NOT_FOUND_DATA.formatMessage("주문"));
        }

        return order;

    }


    @Transactional
    public void postOrders(Long productId, OrderRequestDto orderRequestDto, Member member) throws InterruptedException {

        AtomicInteger retryCount = new AtomicInteger(0);
        final int MAX_RETRIES = 3;

        Product product = null;
        while (true) {
            if (redisLock.tryLock("orderLock", 5)) {
                try {

                    product = productRepository.findById(productId).orElseThrow(
                            () -> new ResponseStatusException(NOT_FOUND_DATA.getStatus(), NOT_FOUND_DATA.formatMessage("상품"))
                    );

                    if (!OpenRunStatus.OPEN.equals(product.getStatus())) {
                        throw new ResponseStatusException(NOT_FOUND_DATA.getStatus(), INVALID_CONDITION.formatMessage("오픈런 상품이 아닙니다"));
                    }

                    if (product.getCurrentQuantity() < orderRequestDto.count()) {
                        throw new ResponseStatusException(NOT_FOUND_DATA.getStatus(), NOT_FOUND_DATA.formatMessage("재고 부족"));
                    }

                    product.decreaseProductQuantity(orderRequestDto.count());

                } finally {
                    redisLock.unlock("orderLock");
                    break;
                }
            } else if (retryCount.incrementAndGet() >= MAX_RETRIES) {
                throw new ResponseStatusException(NOT_AUTHORIZATION.getStatus(), NOT_AUTHORIZATION.formatMessage("주문 실패"));
            } else {
                Thread.sleep(500);
            }

        }


        OrderEventDto orderEventDto = new OrderEventDto(product, orderRequestDto, member);

        orderCreateProducer.createOrder(orderEventDto);
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

        productRepository.updateProductQuantity(order.getCount(), order.getProduct().getId());

        orderRepository.delete(order);
    }
}
