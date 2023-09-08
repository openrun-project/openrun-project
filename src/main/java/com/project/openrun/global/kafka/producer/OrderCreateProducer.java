package com.project.openrun.global.kafka.producer;


import com.project.openrun.global.kafka.dto.OrderEventDto;
import com.project.openrun.orders.entity.Order;
import com.project.openrun.orders.entity.OrderStatus;
import com.project.openrun.orders.repository.OrderRepository;
import com.project.openrun.product.dto.OpenRunProductResponseDto;
import com.project.openrun.product.entity.Product;
import com.project.openrun.product.repository.CacheRedisRepository;
import com.project.openrun.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.util.concurrent.CompletableFuture;

import static com.project.openrun.global.exception.type.ErrorCode.NOT_FOUND_DATA;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderCreateProducer {

    private final KafkaTemplate<Long, OrderEventDto> orderEventDtoKafkaTemplate;
    private final CacheRedisRepository<OpenRunProductResponseDto> openRunProductRedisRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private static final int RETRY_COUNT = 3;

    @Value("${kafka.topic.notification}")
    private String topic;

    public void createOrder(OrderEventDto orderEventDto) {
        CompletableFuture<SendResult<Long, OrderEventDto>> future = orderEventDtoKafkaTemplate.send(topic, orderEventDto);


        future.whenComplete((result, ex) -> {

            if (ex != null) {
                long offset = result.getRecordMetadata().offset();
                int partition = result.getRecordMetadata().partition();

                log.info("Sent error message with offset=[{}] and partition =[{}]", offset, partition);

                retrySend(result.getProducerRecord().value(), RETRY_COUNT);
            }
        });
    }

    private void retrySend(OrderEventDto orderEventDto, int count) {
        for (int i = 0; i < count; i++) {
            try {
                orderEventDtoKafkaTemplate.send("test", orderEventDto);
                return;
            } catch (Exception e) {
                log.error("Exception : {}, This RetryCont is {} ", e.getMessage(), i + 1);
            }
        }

        openRunProductRedisRepository.increaseQuantity(orderEventDto.getProductId(), orderEventDto.getOrderRequestDto().count());

        Product product = productRepository.findById(orderEventDto.getProductId()).orElseThrow(() ->
                new ResponseStatusException(NOT_FOUND_DATA.getStatus(), NOT_FOUND_DATA.formatMessage("존재하지 않는 상품"))
        );

        Order order = Order.builder()
                .member(orderEventDto.getMember())
                .product(product)
                .count(count)
                .totalPrice(product.getPrice() * count)
                .orderStatus(OrderStatus.FAIL)
                .build();

        orderRepository.save(order);

    }
}
