package ua.habatynchik.authenticationservice.service;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import ua.habatynchik.authenticationservice.exception.EmailAlreadyExistsException;
import ua.habatynchik.authenticationservice.exception.UserAlreadyExistsException;
import ua.habatynchik.authenticationservice.model.Role;
import ua.habatynchik.authenticationservice.model.User;
import ua.habatynchik.authenticationservice.repository.RoleRepository;
import ua.habatynchik.authenticationservice.repository.UserRepository;

import java.util.Optional;

@Service
@AllArgsConstructor
@Log4j2
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> userOptional = userRepository.findByUsername(username);

        return userOptional.orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
    }

    @Transactional
    public User registerNewAccount(UserDto userDto) throws EmailAlreadyExistsException, UserAlreadyExistsException {
        validateUserDto(userDto);

        User user = new User()
                .setUsername(userDto.getEmail())
                .setEmail(userDto.getEmail())
                .setFirstName(userDto.getFirstName())
                .setSecondName(userDto.getSecondName())
                .setPassword(new BCryptPasswordEncoder().encode(userDto.getPassword()))
                .setRole(roleRepository.findByRoleEnum(Role.RoleEnum.CLIENT));

        log.info("New account '{}' has been created", user);
        return userRepository.save(user);
    }

    private void validateUserDto(UserDto userDto) throws EmailAlreadyExistsException, UserAlreadyExistsException {
        if (isEmailUnique(userDto.getEmail())) {
            log.info("Email {} is reserved", userDto.getEmail());
            throw new EmailAlreadyExistsException();
        }

        if (isUsernameUnique(userDto.getUsername())) {
            log.info("Username {} is reserved", userDto.getUsername());
            throw new UserAlreadyExistsException();
        }
    }

    private boolean isEmailUnique(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    private boolean isUsernameUnique(String username) {
        return userRepository.findByUsername(username).isPresent();
    }
}
