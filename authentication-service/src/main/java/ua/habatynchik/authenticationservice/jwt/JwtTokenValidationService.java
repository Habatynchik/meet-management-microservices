package ua.habatynchik.authenticationservice.jwt;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.stereotype.Service;
import ua.habatynchik.authenticationservice.exception.InvalidTokenException;
import ua.habatynchik.authenticationservice.service.UserService;

@Service
@AllArgsConstructor
public class JwtTokenValidationService {

    private JwtTokenProvider jwtTokenProvider;
    private UserService userService;

    public String validateToken(String token) throws UsernameNotFoundException, ExpiredJwtException, InvalidTokenException {
        try {
            String username = jwtTokenProvider.getUsernameFromToken(token);
            UserDetails user = userService.loadUserByUsername(username);

            return user.getUsername();
        } catch (ExpiredJwtException | UsernameNotFoundException | InvalidTokenException e) {
            throw e;
        }
    }
}
