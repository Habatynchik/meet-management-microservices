package ua.habatynchik.authenticationservice.service;

import org.springframework.stereotype.Service;
import ua.habatynchik.authenticationservice.model.User;

@Service
public interface UserService {
    public void saveUser(User user);
    public User getUserByNameAndPassword(String name, String password) throws UserNotFoundException;
}
