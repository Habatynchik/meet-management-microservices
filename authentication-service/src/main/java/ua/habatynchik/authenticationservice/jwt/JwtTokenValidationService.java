package ua.habatynchik.authenticationservice.jwt;

import io.jsonwebtoken.ExpiredJwtException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ua.habatynchik.authenticationservice.exception.InvalidTokenException;
import ua.habatynchik.authenticationservice.exception.UserNotFoundException;
import ua.habatynchik.authenticationservice.model.User;
import ua.habatynchik.authenticationservice.service.UserService;

@Service
@AllArgsConstructor
public class JwtTokenValidationService {

    private JwtTokenProvider jwtTokenProvider;
    private UserService userService;

    public Long validateToken(String token) throws UserNotFoundException, ExpiredJwtException, InvalidTokenException {
        try {
            Long userId = jwtTokenProvider.getUserIdFromToken(token);
            User user = userService.loadUserByUserId(userId);

            return user.getId();
        } catch (ExpiredJwtException | UserNotFoundException | InvalidTokenException e) {
            throw e;
        }
    }
}
