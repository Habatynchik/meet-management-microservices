package ua.habatynchik.authenticationservice.service;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ua.habatynchik.authenticationservice.dto.UserLoginDto;
import ua.habatynchik.authenticationservice.dto.UserRegistrationDto;
import ua.habatynchik.authenticationservice.exception.EmailAlreadyExistsException;
import ua.habatynchik.authenticationservice.exception.PasswordMatchException;
import ua.habatynchik.authenticationservice.exception.UserAlreadyExistsException;

@Service
@Log4j2
@AllArgsConstructor
public class AuthenticationService {

    private final UserService userService;

    @KafkaListener(
            topics = "${spring.kafka.topic.reg-request}",
            groupId = "${spring.kafka.consumer.group-id.group-common}",
            containerFactory = "userRegistrationKafkaListenerContainerFactory"
    )
    @SendTo("${spring.kafka.topic.reg-response}")
    public String processRegistrationRequest(ConsumerRecord<String, UserRegistrationDto> record) {
        UserRegistrationDto userRegistrationDto = record.value();

        log.info(record);

        try {
            userService.registerNewAccount(userRegistrationDto);
            return "User has been registered successfully";
        } catch (EmailAlreadyExistsException e) {
            return "User with this email already exists";
        } catch (UserAlreadyExistsException e) {
            return "User with this username already exists";
        } catch (PasswordMatchException e) {
            return "Passwords don't match";
        }
    }

    @KafkaListener(
            topics = "${spring.kafka.topic.auth-request}",
            groupId = "${spring.kafka.consumer.group-id.group-common}",
            containerFactory = "userLoginKafkaListenerContainerFactory"
    )
    @SendTo("${spring.kafka.topic.auth-response}")
    public String processAuthenticationRequest(ConsumerRecord<String, UserLoginDto> record) {

        log.info(record);

        UserLoginDto userLoginDto = record.value();

        try {
            return userService.authenticate(userLoginDto);
        } catch (UsernameNotFoundException e){
            return "User not found";
        } catch (BadCredentialsException e){
            return "Invalid username/password";
        }

    }

}
