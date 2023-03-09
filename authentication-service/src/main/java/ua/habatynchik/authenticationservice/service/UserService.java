package ua.habatynchik.authenticationservice.service;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import ua.habatynchik.authenticationservice.config.JwtTokenProvider;
import ua.habatynchik.authenticationservice.dto.UserLoginDto;
import ua.habatynchik.authenticationservice.dto.UserRegistrationDto;
import ua.habatynchik.authenticationservice.exception.EmailAlreadyExistsException;
import ua.habatynchik.authenticationservice.exception.PasswordMatchException;
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
    private final JwtTokenProvider jwtTokenProvider;

    public String authenticate(UserLoginDto userLoginDto)  throws AuthenticationException {
        User user = userRepository.findByUsernameOrEmail(userLoginDto.getLogin(), userLoginDto.getLogin())
                .orElseThrow(()-> new UsernameNotFoundException("User not found with login or email: " + userLoginDto.getLogin()));

        if (!BCrypt.checkpw(userLoginDto.getPassword(), user.getPassword())) {
            throw new BadCredentialsException("Invalid username/password");
        }

        UserDetails userDetails = loadUserByUsername(user.getUsername());

        return jwtTokenProvider.generateToken(userDetails);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> userOptional = userRepository.findByUsername(username);

        return userOptional.orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
    }

    public UserDetails loadUserByEmail(String email) throws UsernameNotFoundException {
        Optional<User> userOptional = userRepository.findByEmail(email);

        return userOptional.orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
    }

    @Transactional
    public User registerNewAccount(UserRegistrationDto userRegistrationDto) throws EmailAlreadyExistsException, UserAlreadyExistsException, PasswordMatchException {
        validateUserRegistrationDto(userRegistrationDto);

        User user = new User()
                .setUsername(userRegistrationDto.getUsername())
                .setEmail(userRegistrationDto.getEmail())
                .setFirstName(userRegistrationDto.getFirstName())
                .setSecondName(userRegistrationDto.getSecondName())
                .setPassword(new BCryptPasswordEncoder().encode(userRegistrationDto.getPassword()))
                .setRole(roleRepository.findByRoleEnum(Role.RoleEnum.CLIENT));

        log.info("New account '{}' has been created", user);
        return userRepository.save(user);
    }


    private void validateUserRegistrationDto(UserRegistrationDto userRegistrationDto) throws EmailAlreadyExistsException, UserAlreadyExistsException, PasswordMatchException {

        if (isEmailUnique(userRegistrationDto.getEmail())) {
            log.info("Email {} is reserved", userRegistrationDto.getEmail());
            throw new EmailAlreadyExistsException();
        }
        if (isUsernameUnique(userRegistrationDto.getUsername())) {
            log.info("Username {} is reserved", userRegistrationDto.getUsername());
            throw new UserAlreadyExistsException();
        }
        if (!userRegistrationDto.getPassword().equals(userRegistrationDto.getConfirmPassword())) {
            log.info("Passwords don't match");
            throw new PasswordMatchException();
        }
    }

    private boolean isEmailUnique(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    private boolean isUsernameUnique(String username) {
        return userRepository.findByUsername(username).isPresent();
    }
}
