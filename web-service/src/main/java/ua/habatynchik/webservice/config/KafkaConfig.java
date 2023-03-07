package ua.habatynchik.webservice.config;

import lombok.AllArgsConstructor;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import ua.habatynchik.webservice.dto.UserRegistrationDto;
import ua.habatynchik.webservice.dto.serialization.UserRegistrationSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
@AllArgsConstructor
public class KafkaConfig {

    private final KafkaProperties kafkaProperties;
    private final UserRegistrationSerializer userRegistrationSerializer;

    @Bean
    public ProducerFactory<String, UserRegistrationDto> producerFactory() {
        Map<String, Object> configProps = new HashMap<>(kafkaProperties.buildProducerProperties());
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, UserRegistrationSerializer.class);
        return new DefaultKafkaProducerFactory<>(configProps);
    }

    @Bean
    public KafkaTemplate<String, UserRegistrationDto> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory(), true);
    }
}
