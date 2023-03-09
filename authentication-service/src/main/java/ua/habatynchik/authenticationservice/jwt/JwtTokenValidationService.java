package ua.habatynchik.authenticationservice.jwt;

import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import ua.habatynchik.authenticationservice.service.UserService;

@Service
@AllArgsConstructor
public class JwtTokenValidationService {

    private JwtTokenProvider jwtTokenProvider;
    private UserService userService;

    public boolean validateToken(String token) {
        String username = jwtTokenProvider.getUsernameFromToken(token);
        UserDetails user = userService.loadUserByUsername(username);
        return user != null && !jwtTokenProvider.isTokenExpired(token);
    }
}
