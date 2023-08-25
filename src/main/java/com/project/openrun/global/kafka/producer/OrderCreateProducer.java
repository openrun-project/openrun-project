package com.project.openrun.global.kafka.producer;


import com.project.openrun.global.kafka.producer.dto.OrderEventDto;
import com.project.openrun.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderCreateProducer {

    private final KafkaTemplate<Long, OrderEventDto> orderEventDtoKafkaTemplate;
    private final ProductRepository productRepository;
    private static final int RETRY_COUNT = 3;


    public void createOrder(OrderEventDto orderEventDto) {
        CompletableFuture<SendResult<Long, OrderEventDto>> future = orderEventDtoKafkaTemplate.send("test", orderEventDto);


        future.whenComplete((result, ex) -> {

            if (ex != null) {
                long offset = result.getRecordMetadata().offset();
                int partition = result.getRecordMetadata().partition();

                log.info("Sent error message with offset=[{}] and partition =[{}]",offset,partition);

                retrySend(result.getProducerRecord().value(),RETRY_COUNT);
            }
        });
    }
    // @Retry & @Recover 활용 고려.
    private void retrySend(OrderEventDto orderEventDto, int count) {
        for (int i = 0; i < count; i++) {
            try{
                orderEventDtoKafkaTemplate.send("test", orderEventDto);
                return;
            }catch (Exception e){
                log.error("Exception : {}, This RetryCont is {} ", e.getMessage(),i+1);
            }
        }

        productRepository.updateProductQuantity(
                orderEventDto.getOrderRequestDto().count(),
                orderEventDto.getProduct().getId()
        );
    }
}
