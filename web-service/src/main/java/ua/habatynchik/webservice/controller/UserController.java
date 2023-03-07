package ua.habatynchik.webservice.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ua.habatynchik.webservice.dto.UserDto;
import ua.habatynchik.webservice.model.User;
import ua.habatynchik.webservice.service.UserService;

@RestController
@RequestMapping("api/user")
@AllArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody UserDto userDto) {
        System.out.println("dsadas");
        userService.createUser(userDto);
        return new ResponseEntity<>(userDto, HttpStatus.OK);
    }

    @GetMapping("/register")
    public ResponseEntity<?> registerUser() {
        System.out.println("dsadas");
        return new ResponseEntity<>("userDto", HttpStatus.OK);
    }
    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody User user) {
      
            return new ResponseEntity<>(user, HttpStatus.OK);
    }

}
