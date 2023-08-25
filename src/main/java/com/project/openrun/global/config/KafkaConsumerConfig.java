package com.project.openrun.global.config;

import com.project.openrun.global.kafka.consumer.exceptionHandler.CustomErrorHandler;
import com.project.openrun.global.kafka.producer.dto.OrderEventDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.LongDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.util.backoff.FixedBackOff;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@EnableKafka
@Configuration
@RequiredArgsConstructor
public class KafkaConsumerConfig {

    @Value("${kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${kafka.group-id}")
    private String groupId;

    @Value("${kafka.concurrency-count}")
    private int concurrencyCount;

    @Value("${kafka.poll-timeout}")
    private long pollTimeout;

    @Value("${kafka.max-poll-records}")
    private int maxPollRecords;

    /*나중에 지울게요 => 여기 빈으로 주입 받고 => 아래서 productErrorHandler.handle() or productErrorHandler::handle 을 통해서 구현체에서 작동*/
    private final CustomErrorHandler productErrorHandler;


    @Bean
    public KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<Long, OrderEventDto>> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<Long, OrderEventDto> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        factory.setConcurrency(concurrencyCount);
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.RECORD);
        factory.getContainerProperties().setPollTimeout(pollTimeout);

        factory.setCommonErrorHandler(getDefaultErrorHandler());

        return factory;
    }

    @Bean
    public ConsumerFactory<Long, OrderEventDto> consumerFactory() {
        return new DefaultKafkaConsumerFactory<>(consumerConfigs());
    }

    @Bean
    public Map<String, Object> consumerConfigs() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, LongDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        props.put(JsonDeserializer.VALUE_DEFAULT_TYPE, OrderEventDto.class.getName());
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, maxPollRecords);
        return props;
    }

    private DefaultErrorHandler getDefaultErrorHandler() {
        return new DefaultErrorHandler(productErrorHandler::handle, new FixedBackOff(2000L, 3L));
    }
}

