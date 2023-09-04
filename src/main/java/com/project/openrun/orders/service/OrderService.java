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

import static com.project.openrun.global.exception.type.ErrorCode.NOT_AUTHORIZATION;
import static com.project.openrun.global.exception.type.ErrorCode.NOT_FOUND_DATA;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final OrderCreateProducer orderCreateProducer;
    private final OpenRunProductRedisRepositoryImpl openRunProductRedisRepository;



    // fetchJoin 이후에 적용 -> ok
    @Transactional(readOnly = true)
    public Page<OrderResponseDto> getOrders(Member member, Pageable pageable) {

        Page<OrderResponseDto> order = orderRepository.findAllByMember(member, pageable);
        if (order.isEmpty()) {
            throw new ResponseStatusException(NOT_FOUND_DATA.getStatus(), NOT_FOUND_DATA.formatMessage("주문"));
        }

        return order;

    }


    public void postOrders(Long productId, OrderRequestDto orderRequestDto, Member member) {//구매 갯수가 1
        //여기서 가져온 숫자 1
        if(openRunProductRedisRepository.decreaseQuantity(productId, orderRequestDto.count()) < 0){
            //이때 가져온 숫자 0
            openRunProductRedisRepository.increaseQuantity(productId, orderRequestDto.count());
            throw new ResponseStatusException(NOT_FOUND_DATA.getStatus(), NOT_FOUND_DATA.formatMessage("재고 부족"));
        }

        OrderEventDto orderEventDto = new OrderEventDto(productId, orderRequestDto, member);

        orderCreateProducer.createOrder(orderEventDto);


        /*Product product = productRepository.findWithLockById(productId).orElseThrow(
                () -> new ResponseStatusException(NOT_FOUND_DATA.getStatus(), NOT_FOUND_DATA.formatMessage("상품"))
        );

        if (!OpenRunStatus.OPEN.equals(product.getStatus())) {
            throw new ResponseStatusException(NOT_FOUND_DATA.getStatus(), INVALID_CONDITION.formatMessage("오픈런 상품이 아닙니다"));
        }

        if (product.getCurrentQuantity() < orderRequestDto.count()) {
            throw new ResponseStatusException(NOT_FOUND_DATA.getStatus(), NOT_FOUND_DATA.formatMessage("재고 부족"));
        }

        product.decreaseProductQuantity(orderRequestDto.count());

        OrderEventDto orderEventDto = new OrderEventDto(product, orderRequestDto, member);

        orderCreateProducer.createOrder(orderEventDto);*/
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

        // 레디스 복구
        openRunProductRedisRepository.increaseQuantity(order.getProduct().getId(), order.getCount());

        orderRepository.delete(order);
    }
}
