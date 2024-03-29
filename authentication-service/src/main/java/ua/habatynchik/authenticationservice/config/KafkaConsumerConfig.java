package ua.habatynchik.authenticationservice.config;

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
import ua.habatynchik.authenticationservice.dto.UserLoginDto;
import ua.habatynchik.authenticationservice.dto.UserRegistrationDto;
import ua.habatynchik.authenticationservice.dto.deserialization.UserLoginDeserializer;
import ua.habatynchik.authenticationservice.dto.deserialization.UserRegistrationDeserializer;

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

    @Value("${spring.kafka.consumer.group-id.group-jwt}")
    private String groupJWT;

    @Value("${spring.kafka.consumer.group-id.group-refresh}")
    private String groupRefresh;

    public KafkaConsumerConfig(KafkaProperties kafkaProperties) {
        this.kafkaProperties = kafkaProperties;
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, String> jwtRefreshKafkaListenerContainerFactory() {
        return stringConcurrentKafkaListenerContainerFactoryByGroup(groupRefresh);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, String> jwtKafkaListenerContainerFactory() {
        return stringConcurrentKafkaListenerContainerFactoryByGroup(groupJWT);
    }

    private ConcurrentKafkaListenerContainerFactory<String, String> stringConcurrentKafkaListenerContainerFactoryByGroup(String group) {
        ConcurrentKafkaListenerContainerFactory<String, String> factory
                = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(stringConsumerFactory());
        factory.setReplyTemplate(kafkaTemplate());
        factory.setRecordFilterStrategy(consumerRecord -> !Arrays.equals(consumerRecord.headers().lastHeader(KafkaHeaders.GROUP_ID).value(), group.getBytes()));
        return factory;
    }

    @Bean
    public ConsumerFactory<String, String> stringConsumerFactory() {
        Map<String, Object> props = consumerConfigs();
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        return new DefaultKafkaConsumerFactory<>(props);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, UserLoginDto> userLoginKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, UserLoginDto> factory
                = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(userLoginConsumerFactory());
        factory.setReplyTemplate(kafkaTemplate());
        return factory;
    }

    @Bean
    public ConsumerFactory<String, UserLoginDto> userLoginConsumerFactory() {
        Map<String, Object> props = consumerConfigs();

        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, UserLoginDeserializer.class);
        return new DefaultKafkaConsumerFactory<>(props);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, UserRegistrationDto> userRegistrationKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, UserRegistrationDto> factory
                = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(userRegistrationConsumerFactory());
        factory.setReplyTemplate(kafkaTemplate());
        return factory;
    }

    @Bean
    public ConsumerFactory<String, UserRegistrationDto> userRegistrationConsumerFactory() {
        Map<String, Object> props = consumerConfigs();
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, UserRegistrationDeserializer.class);
        return new DefaultKafkaConsumerFactory<>(props);
    }

    @Bean
    public KafkaOperations<String, String> kafkaOperations() {
        return kafkaTemplate();
    }

    @Bean
    public KafkaTemplate<String, String> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }

    @Bean
    public ProducerFactory<String, String> producerFactory() {
        return new DefaultKafkaProducerFactory<>(producerConfigs());
    }

    @Bean
    public Map<String, Object> consumerConfigs() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.GROUP_ID_CONFIG, group);
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        return props;
    }

    private Map<String, Object> producerConfigs() {
        Map<String, Object> props = new HashMap<>(kafkaProperties.buildProducerProperties());
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ProducerConfig.RETRIES_CONFIG, Integer.MAX_VALUE);
        props.put(ProducerConfig.ACKS_CONFIG, "1");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        return props;
    }
}
