package ua.habatynchik.userservice.config;

import lombok.extern.log4j.Log4j2;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.*;
import org.springframework.kafka.support.KafkaHeaders;
import ua.habatynchik.userservice.dto.UserDto;
import ua.habatynchik.userservice.dto.serialization.UserSerializer;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Configuration
@Log4j2
public class KafkaConsumerConfig {
    private final KafkaProperties kafkaProperties;

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${spring.kafka.consumer.group-id.group-common}")
    private String group;

    @Value("${spring.kafka.consumer.group-id.get-user-group}")
    private String getUserGroup;

    public KafkaConsumerConfig(KafkaProperties kafkaProperties) {
        this.kafkaProperties = kafkaProperties;
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, String> getUserGroupListenerContainerFactory() {
        return stringConcurrentKafkaListenerContainerFactoryByGroup(getUserGroup);
    }

    private ConcurrentKafkaListenerContainerFactory<String, String> stringConcurrentKafkaListenerContainerFactoryByGroup(String group) {
        ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(stringConsumerFactory());
        factory.setReplyTemplate(kafkaTemplateForUserDto());
        factory.setRecordFilterStrategy(consumerRecord -> !Arrays.equals(consumerRecord.headers().lastHeader(KafkaHeaders.GROUP_ID).value(), group.getBytes()));
        return factory;
    }

    private ConsumerFactory<String, String> stringConsumerFactory() {
        Map<String, Object> props = consumerConfigs();
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        return new DefaultKafkaConsumerFactory<>(props);
    }

    @Bean
    public KafkaTemplate<String, UserDto> kafkaTemplateForUserDto() {
        return new KafkaTemplate<>(producerFactoryForUserDto());
    }

    private ProducerFactory<String, UserDto> producerFactoryForUserDto() {
        return new DefaultKafkaProducerFactory<>(producerConfigs(), new StringSerializer(), new UserSerializer());
    }

    private Map<String, Object> consumerConfigs() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.GROUP_ID_CONFIG, group);
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        return props;
    }

    private Map<String, Object> producerConfigs() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.RETRIES_CONFIG, Integer.MAX_VALUE);
        props.put(ProducerConfig.ACKS_CONFIG, "1");
        return props;
    }
}
