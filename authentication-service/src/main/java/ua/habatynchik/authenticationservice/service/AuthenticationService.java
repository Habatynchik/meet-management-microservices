package ua.habatynchik.authenticationservice.service;

import lombok.extern.log4j.Log4j2;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import ua.habatynchik.authenticationservice.dto.UserRegistrationDto;


@Service
@Log4j2
public class AuthenticationService {

  //  private KafkaTemplate<String, String> kafkaTemplate;

    @Value("${spring.kafka.topic.auth-response}")
    private String authResponseTopic;

    private final UserService userService;

    public AuthenticationService(UserService userService) {
        this.userService = userService;
    }

    @KafkaListener(topics = "${spring.kafka.topic.auth-request}", groupId = "${spring.kafka.consumer.group-id}")
    public void processRegistrationRequest(ConsumerRecord<String, UserRegistrationDto> record) {

        log.info(record.value());
     /*
     // not working
     UserRegistrationDto userRegistrationDto = (UserRegistrationDto) record.value();
     * */

     /*   UserRegistrationDto userRegistrationDto = record.value();

        try {
            userService.registerNewAccount(userRegistrationDto);
        } catch (EmailAlreadyExistsException e) {
            sendResponse("User with this email already exists", authResponseTopic);
            return;
        } catch (UserAlreadyExistsException e) {
            sendResponse("User with this username already exists", authResponseTopic);
            return;
        } catch (PasswordMatchException e) {
            sendResponse("Passwords don't match", authResponseTopic);
            return;
        }

        sendResponse("User has been registered successfully", authResponseTopic);*/
    }

    private void sendResponse(String message, String topic) {
       //kafkaTemplate.send(topic, message);
    }

}
