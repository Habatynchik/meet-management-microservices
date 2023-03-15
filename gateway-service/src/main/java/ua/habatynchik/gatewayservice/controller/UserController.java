package ua.habatynchik.gatewayservice.controller;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ua.habatynchik.gatewayservice.dto.UserDto;
import ua.habatynchik.gatewayservice.service.UserService;

@RestController
@RequestMapping("api/user")
@AllArgsConstructor
@Log4j2
public class UserController {

    UserService userService;

    @GetMapping("/get")
    public ResponseEntity<?> getUser(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader,
                                     @RequestBody String username) {

        log.info(username);
        UserDto response = userService.getUserByUsername(username);

        return ResponseEntity.ok()
                .body(response);
    }

}
