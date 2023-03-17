package ua.habatynchik.gatewayservice.controller;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ua.habatynchik.gatewayservice.HeaderParseException;
import ua.habatynchik.gatewayservice.dto.UserDto;
import ua.habatynchik.gatewayservice.service.TokenService;
import ua.habatynchik.gatewayservice.service.UserService;

@RestController
@RequestMapping("api/user")
@AllArgsConstructor
@Log4j2
public class UserController {

    private final UserService userService;
    private final TokenService tokenService;

    @GetMapping("/get/me")
    public ResponseEntity<?> getMe(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader) {
        Long userId;

        try {
            userId = getUserIdFromAuthorizationHeader(authorizationHeader);
        } catch (HeaderParseException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }

        UserDto response = userService.getUserById(userId);

        return ResponseEntity.ok().body(response);
    }

    @RequestMapping(value = "/get", method = RequestMethod.GET, params = "username")
    public ResponseEntity<?> getUserByUsername(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader,
                                               @RequestParam String username) {
        Long userId;
        try {
            userId = getUserIdFromAuthorizationHeader(authorizationHeader);
        } catch (HeaderParseException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }

        UserDto response = userService.getUserByUsername(username);

        if (userService.hasRole(userId, "ADMIN") ||
                userService.hasRole(userId, "CLIENT") && userId.equals(response.getId())) {
            return ResponseEntity.ok().body(response);
        }

        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();

    }

    @RequestMapping(value = "/get", method = RequestMethod.GET, params = "id")
    public ResponseEntity<?> getUserById(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader,
                                         @RequestParam Long id) {

        Long userId;
        try {
            userId = getUserIdFromAuthorizationHeader(authorizationHeader);
        } catch (HeaderParseException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }

        UserDto response = userService.getUserById(id);

        if (userService.hasRole(userId, "ADMIN") ||
                userService.hasRole(userId, "CLIENT") && userId.equals(response.getId())) {
            return ResponseEntity.ok().body(response);
        }

        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }


    private Long getUserIdFromAuthorizationHeader(String authorizationHeader) throws HeaderParseException {
        String token = authorizationHeader.substring(7);

        Long userId;
        String result = null;
        try {
            result = tokenService.validateToken(token);
            return userId = Long.valueOf(result);
        } catch (NumberFormatException e) {
            throw new HeaderParseException(result);
        }
    }
}
