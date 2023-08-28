package com.project.openrun.orders.service;

import com.project.openrun.global.kafka.dto.OrderEventDto;
import com.project.openrun.member.entity.Member;
import com.project.openrun.orders.dto.OrderRequestDto;
import com.project.openrun.product.entity.OpenRunStatus;
import com.project.openrun.product.entity.Product;
import com.project.openrun.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import static com.project.openrun.global.exception.type.ErrorCode.*;

@Service
@RequiredArgsConstructor
public class OrderServiceFacade {

    private final ProductRepository productRepository;


    @Transactional
    public OrderEventDto CheckOrderPossibility(Long productId, OrderRequestDto orderRequestDto, Member member){
                Product product = productRepository.findWithOptimisticLockById(productId).orElseThrow(
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

        return orderEventDto;
    }
}
