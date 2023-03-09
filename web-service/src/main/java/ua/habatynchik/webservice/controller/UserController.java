package ua.habatynchik.webservice.controller;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ua.habatynchik.webservice.dto.UserLoginDto;
import ua.habatynchik.webservice.dto.UserRegistrationDto;
import ua.habatynchik.webservice.service.LoginService;
import ua.habatynchik.webservice.service.RegistrationService;

@RestController
@RequestMapping("api/user")
@AllArgsConstructor
@Log4j2
public class UserController {
    private final RegistrationService registrationService;
    private final LoginService loginService;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody UserRegistrationDto userRegistrationDto) {
        String response = registrationService.registerUser(userRegistrationDto);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody UserLoginDto userLoginDto) {
        log.info(userLoginDto);
        String response =  loginService.loginUser(userLoginDto);
        log.info(response);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
