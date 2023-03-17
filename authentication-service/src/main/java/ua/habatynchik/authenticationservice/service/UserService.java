package ua.habatynchik.authenticationservice.service;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import ua.habatynchik.authenticationservice.exception.UserNotFoundException;
import ua.habatynchik.authenticationservice.jwt.JwtTokenProvider;
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

    public String authenticate(UserLoginDto userLoginDto) throws UsernameNotFoundException, BadCredentialsException {
        User user = userRepository.findByUsernameOrEmail(userLoginDto.getLogin(), userLoginDto.getLogin())
                .orElseThrow(() -> new UsernameNotFoundException("User not found with login or email: " + userLoginDto.getLogin()));

        if (!BCrypt.checkpw(userLoginDto.getPassword(), user.getPassword())) {
            throw new BadCredentialsException("Invalid username/password");
        }

        return jwtTokenProvider.generateToken(user);
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

    public User loadUserByUserId(Long userId) throws UserNotFoundException {
        Optional<User> userOptional = userRepository.findById(userId);

        return userOptional.orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));
    }

    @Transactional
    public User registerNewAccount(UserRegistrationDto userRegistrationDto) throws EmailAlreadyExistsException, UserAlreadyExistsException, PasswordMatchException {
        validateUserRegistrationDto(userRegistrationDto);

        Optional<User> existingUser = userRepository.findByEmail(userRegistrationDto.getEmail());
        User user = existingUser.orElseGet(User::new);

        user.setUsername(userRegistrationDto.getUsername());
        user.setEmail(userRegistrationDto.getEmail());
        user.setFirstName(userRegistrationDto.getFirstName());
        user.setSecondName(userRegistrationDto.getSecondName());
        user.setPassword(new BCryptPasswordEncoder().encode(userRegistrationDto.getPassword()));
        user.setRole(roleRepository.findByRoleEnum(Role.RoleEnum.CLIENT));

        log.info("New account '{}' has been created", user);
        return userRepository.save(user);
    }


    private void validateUserRegistrationDto(UserRegistrationDto userRegistrationDto) throws EmailAlreadyExistsException, UserAlreadyExistsException, PasswordMatchException {

        if (isEmailUnique(userRegistrationDto.getEmail()) && !isGuest(userRegistrationDto.getEmail())) {
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

    private boolean isGuest(String email) {
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isPresent()) {
            Role userRole = roleRepository.findByRoleEnum(user.get().getRole().getRoleEnum());
            log.info(userRole.getRoleEnum().equals(Role.RoleEnum.GUEST));
            return userRole.getRoleEnum().equals(Role.RoleEnum.GUEST);
        }
        return false;
    }

    private boolean isEmailUnique(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    private boolean isUsernameUnique(String username) {
        return userRepository.findByUsername(username).isPresent();
    }
}
