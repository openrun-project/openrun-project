package com.project.openrun.global.kafka.consumer.exceptionHandler;

import com.project.openrun.global.kafka.producer.dto.OrderEventDto;
import com.project.openrun.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.stereotype.Component;

@Slf4j
@Component(value = "productErrorHandler")
@RequiredArgsConstructor
public class ProductErrorHandler implements CustomErrorHandler {

    private final ProductRepository productRepository;

    @Override
    public void handle(ConsumerRecord<?, ?> consumerRecord, Exception exception) {
        log.info("Listner에서 주문 저장에 실패했습니다. n번 실패");
        OrderEventDto orderEventDto = (OrderEventDto) consumerRecord.value();
        productRepository.updateProductQuantity(orderEventDto.getOrderRequestDto().count(), orderEventDto.getProduct().getId());
    }
}