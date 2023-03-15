package ua.habatynchik.userservice.service;

import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ua.habatynchik.userservice.dto.UserDto;
import ua.habatynchik.userservice.model.User;
import ua.habatynchik.userservice.repository.RoleRepository;
import ua.habatynchik.userservice.repository.UserRepository;

import java.util.Optional;

@Service
@AllArgsConstructor
public class UserService {
    UserRepository userRepository;
    RoleRepository roleRepository;

    public UserDto getUserDtoByUsername(String username) throws UsernameNotFoundException {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));


        return UserDto.builder()
                .username(user.getUsername())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .secondName(user.getSecondName())
                .role(UserDto.RoleEnum.valueOf(user.getRole().getRoleEnum().name()))
                .build();

    }

}
