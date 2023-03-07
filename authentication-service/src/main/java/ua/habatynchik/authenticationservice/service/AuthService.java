package ua.habatynchik.authenticationservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@Log4j2
public class AuthService {

    private KafkaTemplate<String, String> kafkaTemplate;

    @Value("${spring.kafka.topic.auth-request}")
    private String authRequestTopic;

    @Value("${spring.kafka.topic.auth-response}")
    private String authResponseTopic;

    @KafkaListener(topics = "${spring.kafka.topic.auth-request}", groupId = "${spring.kafka.consumer.group-id}")
    public void processAuthRequest(String userJson) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            UserDto userDto = mapper.readValue(userJson, UserDto.class);

            log.info(userDto.getUsername());
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }



        // Отправляем ответ на топик auth-response в формате JSON
        // kafkaTemplate.send(authResponseTopic, userDto);
    }
}
