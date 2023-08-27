package com.project.openrun.global.kafka.consumer.exceptionHandler;

import org.apache.kafka.clients.consumer.ConsumerRecord;

public interface CustomErrorHandler {
    void handle(ConsumerRecord<?, ?> consumerRecord, Exception exception);
}
