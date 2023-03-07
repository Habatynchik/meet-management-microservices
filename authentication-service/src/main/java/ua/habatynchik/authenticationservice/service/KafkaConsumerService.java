package ua.habatynchik.authenticationservice.service;

import lombok.extern.log4j.Log4j2;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Log4j2
public class KafkaConsumerService {

    @KafkaListener(topics = "${spring.kafka.topic.jwk-request}", groupId = "${spring.kafka.consumer.group-id}")
    public void consumeJwkRequest(String message) {
        log.info(String.format("Received JWK request message: %s", message));

        // Обработка сообщения
    }
}
