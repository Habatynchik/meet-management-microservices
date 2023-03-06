package ua.habatynchik.authenticationservice.controller;


import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ua.habatynchik.authenticationservice.config.JwtGeneratorInterface;
import ua.habatynchik.authenticationservice.model.Role;
import ua.habatynchik.authenticationservice.model.User;
import ua.habatynchik.authenticationservice.service.UserService;

@RestController
@RequestMapping("api/v1/user")
@AllArgsConstructor
public class UserController {

    private final UserService userService;
    private final JwtGeneratorInterface jwtGenerator;




    @PostMapping("/register")
    public ResponseEntity<?> postUser(@RequestBody User user) {

        System.out.println(user);
        try {
            userService.saveUser(user);
            return new ResponseEntity<>(user, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
        }


    }

    @GetMapping("/register")
    public ResponseEntity<?> getUser() {
        return new ResponseEntity<>(new User().setUsername("Nik").setRole(Role.builder().roleEnum(Role.RoleEnum.CLIENT).build()), HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody User user) {
        try {
            if (user.getUsername() == null || user.getPassword() == null) {
                throw new UserNotFoundException("UserName or Password is Empty");
            }
            User userData = userService.getUserByNameAndPassword(user.getUsername(), user.getPassword());
            if (userData == null) {
                throw new UserNotFoundException("UserName or Password is Invalid");
            }
            return new ResponseEntity<>(jwtGenerator.generateToken(user), HttpStatus.OK);
        } catch (UserNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
        }
    }


    @PostMapping("/test")
    public String loginUser(@RequestParam String user) {
        return user;
    }

}
