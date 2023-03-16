package ua.habatynchik.authenticationservice.service;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import ua.habatynchik.authenticationservice.exception.InvalidTokenException;
import ua.habatynchik.authenticationservice.exception.UserNotFoundException;
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

        try {
            return tokenProvider.refreshToken(token);
        } catch (InvalidTokenException e) {
            log.error("Invalid JWT token", e);
            return "Error: Invalid JWT token";
        }
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
            return tokenValidationService.validateToken(token).toString();

        } catch (UserNotFoundException e) {
            log.error("User not found", e);
            return "User not found";
        } catch (ExpiredJwtException e) {
            log.error("Token expired", e);
            return "Token expired";
        }  catch (InvalidTokenException e) {
            log.error("Invalid JWT token", e);
            return "Invalid JWT token";
        } catch (Exception e) {
            log.error("Unexpected error", e);
            return "Unexpected error";
        }

    }
}