package ua.habatynchik.webservice.controller;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ua.habatynchik.webservice.dto.UserLoginDto;
import ua.habatynchik.webservice.dto.UserRegistrationDto;
import ua.habatynchik.webservice.service.LoginService;
import ua.habatynchik.webservice.service.RegistrationService;

import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api/user")
@AllArgsConstructor
@Log4j2
public class UserController {
    private final RegistrationService registrationService;
    private final LoginService loginService;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody @Valid UserRegistrationDto userRegistrationDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return handleValidationError(bindingResult);
        }

        String response = registrationService.registerUser(userRegistrationDto);

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody @Valid UserLoginDto userLoginDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return handleValidationError(bindingResult);
        }

        String jwt = loginService.loginUser(userLoginDto);
        return ResponseEntity.ok()
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwt)
                .body(jwt);
    }

    private ResponseEntity<?> handleValidationError(BindingResult bindingResult) {
        Map<String, String> errors = bindingResult.getAllErrors()
                .stream()
                .collect(Collectors.toMap(
                        error -> ((FieldError) error).getField(),
                        error -> error.getDefaultMessage()
                ));
        return ResponseEntity.badRequest().body(errors);
    }
}
