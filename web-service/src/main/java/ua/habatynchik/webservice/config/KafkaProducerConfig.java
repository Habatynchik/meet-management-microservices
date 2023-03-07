package ua.habatynchik.webservice.config;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
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

    @Bean
    public ProducerFactory<String, UserLoginDto> userLoginDtoProducerFactory() {
        return new DefaultKafkaProducerFactory<>(producerConfigs(), new StringSerializer(), new UserLoginSerializer());
    }

    @Bean
    public KafkaTemplate<String, UserLoginDto> userLoginDtoKafkaTemplate() {
        return new KafkaTemplate<>(userLoginDtoProducerFactory());
    }

    @Bean
    public ProducerFactory<String, UserRegistrationDto> userRegistrationDtoProducerFactory() {
        return new DefaultKafkaProducerFactory<>(producerConfigs(), new StringSerializer(), new UserRegistrationSerializer());
    }

    @Bean
    public KafkaTemplate<String, UserRegistrationDto> userRegistrationDtoKafkaTemplate() {
        return new KafkaTemplate<>(userRegistrationDtoProducerFactory(), true);
    }


    private Map<String, Object> producerConfigs() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        return props;
    }
}
