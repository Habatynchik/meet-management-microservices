package ua.habatynchik.authenticationservice.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Component;
import ua.habatynchik.authenticationservice.jwt.JwtTokenProvider;
import ua.habatynchik.authenticationservice.jwt.JwtTokenValidationService;

@Component
@Slf4j
@AllArgsConstructor
@EnableKafka
public class TokenRefreshListener {

    private final JwtTokenProvider tokenProvider;
    private final JwtTokenValidationService tokenValidationService;

    @KafkaListener(
            topics = "${spring.kafka.topic.jwt-request}",
            groupId = "${spring.kafka.consumer.group-id.group-refresh}",
            containerFactory = "jwtRefreshKafkaListenerContainerFactory"
    )
    @SendTo("${spring.kafka.topic.jwt-response}")
    public String processTokenRefreshRequest(ConsumerRecord<String, String> record) {

        log.info("processTokenRefreshRequest");
        log.info(String.valueOf(record));

        String token = record.value().toString();
        return tokenProvider.refreshToken(token);
    }

    @KafkaListener(
            topics = "${spring.kafka.topic.jwt-request}",
            groupId = "${spring.kafka.consumer.group-id.group-jwt}",
            containerFactory = "jwtKafkaListenerContainerFactory"
    )
    @SendTo("${spring.kafka.topic.jwt-response}")
    public String processValidateRequest(ConsumerRecord<String, String> record) {

        log.info("processValidateRequest");

        String token = record.value().toString();

        if (tokenValidationService.validateToken(token) ){
            return token;
        }

        return "Validation error";
    }
}