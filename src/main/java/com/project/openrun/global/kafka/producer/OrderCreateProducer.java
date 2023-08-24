package com.project.openrun.global.kafka.producer;

import com.project.openrun.global.kafka.producer.dto.OrderEventDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderCreateProducer {

    private final KafkaTemplate<Long, OrderEventDto> orderEventDtoKafkaTemplate;

    /*@Value("${spring.kafka.topic.notification}")
    private final String topic = "orders";*/

    public void createOrder(OrderEventDto orderEventDto){
        //Long partitionNumber = orderEventDto.getProduct().getId() % 10;
        orderEventDtoKafkaTemplate.send("orders", /*partitionNumber,*/ orderEventDto);
    }


}
