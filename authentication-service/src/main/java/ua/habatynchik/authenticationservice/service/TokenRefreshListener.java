package ua.habatynchik.authenticationservice.service;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
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
        String token = record.value().toString();

        try {
            return tokenValidationService.validateToken(token);
        } catch (UsernameNotFoundException e) {
            log.error("Username not found", e);
            return "Error: Username not found";
        } catch (ExpiredJwtException e) {
            log.error("Token expired", e);
            return "Error: Token expired";
        } catch (UnsupportedJwtException e) {
            log.error("Unsupported JWT token", e);
            return "Error: Unsupported JWT token";
        } catch (Exception e) {
            log.error("Unexpected error", e);
            return "Error: Unexpected error";
        }

    }
}