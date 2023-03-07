package ua.habatynchik.webservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import ua.habatynchik.webservice.dto.UserRegistrationDto;

@Service
@Log4j2
@RequiredArgsConstructor
public class UserService {
    private final KafkaTemplate<String, UserRegistrationDto> kafkaTemplate;

    @Value("${spring.kafka.topic.auth-request}")
    private String authRequestTopic;

    public void registerUser(UserRegistrationDto userRegistrationDto) {
        kafkaTemplate.send(authRequestTopic, userRegistrationDto);
    }

    @KafkaListener(topics = "${spring.kafka.topic.auth-response}", groupId = "${spring.kafka.consumer.group-id}")
    public void processRegistrationRequest(ConsumerRecord<String, String> record) {
       log.info(record.value());


    }



}
