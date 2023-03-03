package ua.habatynchik.authenticationservice.model.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import ua.habatynchik.authenticationservice.model.entity.User;
import ua.habatynchik.authenticationservice.model.exeptions.UserNotFoundException;
import ua.habatynchik.authenticationservice.model.repository.UserRepository;

@Service
@RequiredArgsConstructor
@Log4j2
public class UserServiceImpl  implements UserService {
    private final UserRepository userRepository;

    @Override
    public void saveUser(User user) {
        userRepository.save(user);
    }

    @Override
    public User getUserByNameAndPassword(String username, String password) throws UserNotFoundException {
        User user = userRepository.findByUsernameAndPassword(username, password)
                .orElseThrow(() -> new UserNotFoundException("Invalid id and password"));

        return user;
    }
}
