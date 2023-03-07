package ua.habatynchik.webservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import ua.habatynchik.webservice.dto.UserDto;

@Service
@Log4j2
public class UserService {
    private final KafkaTemplate<String, String> kafkaTemplate;

    @Value("${spring.kafka.topic.auth-request}")
    private String authRequestTopic;

    public UserService(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void createUser(UserDto userDto) {
        log.info(userDto.getUsername());

        ObjectMapper mapper = new ObjectMapper();
        String userJson = "";
        try {
             userJson = mapper.writeValueAsString(userDto);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        kafkaTemplate.send(authRequestTopic, userJson);
    }

}
