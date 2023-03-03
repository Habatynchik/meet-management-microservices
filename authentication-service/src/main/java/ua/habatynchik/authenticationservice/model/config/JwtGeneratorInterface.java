package ua.habatynchik.authenticationservice.model.config;

import ua.habatynchik.authenticationservice.model.entity.User;

import java.util.Map;

public interface JwtGeneratorInterface {

    Map<String, String> generateToken(User user);

}
