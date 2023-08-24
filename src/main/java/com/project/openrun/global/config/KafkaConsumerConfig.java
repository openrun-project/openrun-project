package com.project.openrun.global.config;


import com.project.openrun.global.kafka.producer.dto.OrderEventDto;
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
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;

@EnableKafka  // Spring Kafka를 활성화시키는 어노테이션
@Configuration  // Spring에서 이 클래스를 구성 클래스로 간주하게 하는 어노테이션
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

    @Bean
    public KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<Long, OrderEventDto>> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<Long, OrderEventDto> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());  // Consumer 팩토리 설정
        factory.setConcurrency(concurrencyCount);  // 10개의 Consumer 인스턴스가 동시에 실행됨
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.RECORD);  // 각 메시지가 처리될 때마다 커밋
        factory.getContainerProperties().setPollTimeout(pollTimeout);  // poll() 호출의 타임아웃을 5000ms로 설정
        return factory;  // 설정된 factory 반환
    }

    @Bean
    public ConsumerFactory<Long, OrderEventDto> consumerFactory() {
        return new DefaultKafkaConsumerFactory<>(consumerConfigs());  // Consumer 설정을 사용하여 Default Kafka Consumer Factory를 생성
    }

    @Bean
    public Map<String, Object> consumerConfigs() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);  // Kafka 브로커의 주소
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);  // Consumer 그룹 ID 설정
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, LongDeserializer.class);  // 키를 deserialize하는 클래스 설정
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);  // 값을 deserialize하는 클래스 설정
        props.put(JsonDeserializer.VALUE_DEFAULT_TYPE, OrderEventDto.class.getName());
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);  // 자동 커밋 비활성화
        //props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "100");  // 커밋 간격 설정 (현재 주석 처리됨)
        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, maxPollRecords);  // 한 번의 poll() 호출로 최대로 가져올 메시지의 수
        //props.put(ConsumerConfig.PARTITION_ASSIGNMENT_STRATEGY_CONFIG, RoundRobinAssignor.class);  // Partition 할당 전략 설정
        return props;  // 설정된 설정 맵 반환
    }
}