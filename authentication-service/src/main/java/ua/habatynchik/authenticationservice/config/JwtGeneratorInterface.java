package ua.habatynchik.authenticationservice.config;

import ua.habatynchik.authenticationservice.model.User;

import java.util.Map;

public interface JwtGeneratorInterface {

    Map<String, String> generateToken(User user);

}
