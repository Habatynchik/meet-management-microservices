package ua.habatynchik.userservice.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import ua.habatynchik.userservice.dto.UserDto;

@Component
@Slf4j
@AllArgsConstructor
@EnableKafka
public class UserListenerService {

    UserService userService;

    @KafkaListener(
            topics = "${spring.kafka.topic.user-request}",
            groupId = "${spring.kafka.consumer.group-id.get-user-group}",
            containerFactory = "getUserGroupListenerContainerFactory"
    )
    @SendTo("${spring.kafka.topic.user-response}")
    public UserDto processTokenRefreshRequest(ConsumerRecord<String, String> record) {

        String username = record.value().toString();

        try {
            return userService.getUserDtoByUsername(username);
        } catch (UsernameNotFoundException e) {
            log.error(e.toString());
        }
        return null;
    }

}

