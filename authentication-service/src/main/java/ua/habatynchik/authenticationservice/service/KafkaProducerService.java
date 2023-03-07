package ua.habatynchik.authenticationservice.service;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@Log4j2
public class KafkaProducerService {

    private final KafkaTemplate<String, String> kafkaTemplate;

    public void sendAuthResponse(String message) {
        kafkaTemplate.send("${spring.kafka.topic.auth-response}", message);
    }

    public void sendJwkResponse(String message) {
        kafkaTemplate.send("${spring.kafka.topic.jwk-response}", message);
    }

}
