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

    @RequestMapping(value = "/get", method = RequestMethod.GET, params = "username")
    public ResponseEntity<?> getUserByUsername(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader,
                                     @RequestParam String username) {

        UserDto response = userService.getUserByUsername(username);

        return ResponseEntity.ok()
                .body(response);
    }

    @RequestMapping(value = "/get", method = RequestMethod.GET, params = "id")
    public ResponseEntity<?> getUserById(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader,
                                     @RequestParam Long id) {

        UserDto response = userService.getUserById(id);

        return ResponseEntity.ok()
                .body(response);
    }

}
