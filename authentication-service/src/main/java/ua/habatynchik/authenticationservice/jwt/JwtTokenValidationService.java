package ua.habatynchik.authenticationservice.jwt;

import io.jsonwebtoken.ExpiredJwtException;
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

    public String validateToken(String token) throws UsernameNotFoundException, ExpiredJwtException, UnsupportedJwtException {
        try {
            String username = jwtTokenProvider.getUsernameFromToken(token);
            UserDetails user = userService.loadUserByUsername(username);

            return user.getUsername();
        } catch (UsernameNotFoundException e) {
            throw new UsernameNotFoundException("User not found");
        } catch (ExpiredJwtException e) {
            throw new ExpiredJwtException(null, null, "Token expired");
        } catch (UnsupportedJwtException e) {
            throw new UnsupportedJwtException("Unsupported JWT token");
        }
    }
}
