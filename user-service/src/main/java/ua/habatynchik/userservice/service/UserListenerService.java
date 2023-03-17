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
import ua.habatynchik.userservice.exception.UserNotFoundException;

@Component
@Slf4j
@AllArgsConstructor
@EnableKafka
public class UserListenerService {

    UserService userService;

    @KafkaListener(
            topics = "${spring.kafka.topic.user-request}",
            groupId = "${spring.kafka.consumer.group-id.get-user-by-username-group}",
            containerFactory = "getUserByUsernameGroupListenerContainerFactory"
    )
    @SendTo("${spring.kafka.topic.user-response}")
    public UserDto listenAndProcessReturnUserByUsernameRequest(ConsumerRecord<String, String> record) {

        String username = record.value().toString();

        try {
            return userService.getUserDtoByUsername(username);
        } catch (UsernameNotFoundException e) {
            log.error(e.toString());
        }
        return null;
    }

    @KafkaListener(
            topics = "${spring.kafka.topic.user-request}",
            groupId = "${spring.kafka.consumer.group-id.get-user-by-id-group}",
            containerFactory = "getUserByIdGroupListenerContainerFactory"
    )
    @SendTo("${spring.kafka.topic.user-response}")
    public UserDto listenAndProcessReturnUserByIdRequest(ConsumerRecord<String, String> record) {

        Long userId = Long.valueOf(record.value());

        try {
            return userService.getUserDtoById(userId);
        } catch (UserNotFoundException e) {
            log.error(e.toString());
        }

        return null;
    }

    /*@KafkaListener(
            topics = "${spring.kafka.topic.user-request}",
            groupId = "${spring.kafka.consumer.group-id.add-user-group}",
            containerFactory = "addUserGroupListenerContainerFactory"
    )
    @SendTo("${spring.kafka.topic.user-response}")
    public String listenAndProcessAddUserRequest(ConsumerRecord<String, UserDto> record) {
        return null;
    }

    @KafkaListener(
            topics = "${spring.kafka.topic.user-request}",
            groupId = "${spring.kafka.consumer.group-id.update-user-group}",
            containerFactory = "updateUserGroupListenerContainerFactory"
    )
    @SendTo("${spring.kafka.topic.user-response}")
    public String listenAndProcessUpdateUserRequest(ConsumerRecord<String, UserDto> record) {
        return null;
    }*/

}

