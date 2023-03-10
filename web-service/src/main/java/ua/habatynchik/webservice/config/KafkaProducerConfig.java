package ua.habatynchik.webservice.config;


import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;
import ua.habatynchik.webservice.dto.UserLoginDto;
import ua.habatynchik.webservice.dto.UserRegistrationDto;
import ua.habatynchik.webservice.dto.serialization.UserLoginSerializer;
import ua.habatynchik.webservice.dto.serialization.UserRegistrationSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableKafka
public class KafkaProducerConfig {
    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;
    @Value("${spring.kafka.consumer.group-id}")
    private String group;

    @Value("${spring.kafka.topic.auth-response}")
    private String authResponse;

    @Value("${spring.kafka.topic.reg-response}")
    private String regResponse;

    @Value("${spring.kafka.topic.jwt-response}")
    private String jwtResponse;

    @Bean
    public ProducerFactory<String, String> stringProducerFactory() {
        return new DefaultKafkaProducerFactory<>(producerConfigs(), new StringSerializer(), new StringSerializer());
    }

    @Bean
    public ReplyingKafkaTemplate<String, String, String> stringReplyingKafkaTemplate() {
        var producerFactory = new DefaultKafkaProducerFactory<>(producerConfigs(), new StringSerializer(), new StringSerializer());
        var replyContainer = new KafkaMessageListenerContainer<>(stringConsumerFactory(), new ContainerProperties(jwtResponse));
        return new ReplyingKafkaTemplate<>(producerFactory, replyContainer);
    }

    @Bean
    public ProducerFactory<String, UserLoginDto> userLoginDtoProducerFactory() {
        return new DefaultKafkaProducerFactory<>(producerConfigs(), new StringSerializer(), new UserLoginSerializer());
    }

    @Bean
    public ReplyingKafkaTemplate<String, UserLoginDto, String> userLoginDtoReplyingKafkaTemplate() {
        var producerFactory = new DefaultKafkaProducerFactory<>(producerConfigs(), new StringSerializer(), new UserLoginSerializer());
        var replyContainer = new KafkaMessageListenerContainer<>(stringConsumerFactory(), new ContainerProperties(authResponse));
        return new ReplyingKafkaTemplate<>(producerFactory, replyContainer);
    }

    @Bean
    public ProducerFactory<String, UserRegistrationDto> userRegistrationDtoProducerFactory() {
        return new DefaultKafkaProducerFactory<>(producerConfigs(), new StringSerializer(), new UserRegistrationSerializer());
    }

    @Bean
    public ReplyingKafkaTemplate<String, UserRegistrationDto, String> userRegistrationDtoReplyingKafkaTemplate(
    ) {
        var producerFactory = new DefaultKafkaProducerFactory<>(producerConfigs(), new StringSerializer(), new UserRegistrationSerializer());
        var replyContainer = new KafkaMessageListenerContainer<>(stringConsumerFactory(), new ContainerProperties(regResponse));
        return new ReplyingKafkaTemplate<>(producerFactory, replyContainer);
    }


    @Bean
    public ConsumerFactory<String, String> stringConsumerFactory() {
        Map<String, Object> props = consumerConfigs();
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        return new DefaultKafkaConsumerFactory<>(props);
    }


    private Map<String, Object> consumerConfigs() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "group-refresh");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        return props;
    }

    private Map<String, Object> producerConfigs() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.RETRIES_CONFIG, Integer.MAX_VALUE);
        props.put(ProducerConfig.ACKS_CONFIG, "all");
        return props;
    }
}
