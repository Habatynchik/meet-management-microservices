package ua.habatynchik.authenticationservice.model.service;

import org.springframework.stereotype.Service;
import ua.habatynchik.authenticationservice.model.entity.User;
import ua.habatynchik.authenticationservice.model.exeptions.UserNotFoundException;

@Service
public interface UserService {
    public void saveUser(User user);
    public User getUserByNameAndPassword(String name, String password) throws UserNotFoundException;
}
