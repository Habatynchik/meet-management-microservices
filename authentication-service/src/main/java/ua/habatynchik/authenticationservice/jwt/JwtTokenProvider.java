package ua.habatynchik.authenticationservice.jwt;

import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import ua.habatynchik.authenticationservice.exception.InvalidTokenException;
import ua.habatynchik.authenticationservice.model.User;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtTokenProvider implements Serializable {

    @Serial
    private static final long serialVersionUID = -2550185165626007488L;

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private long expiration;


    public String getUsernameFromToken(String token) throws ClaimJwtException, InvalidTokenException {

        try {
            return getClaimFromToken(token, Claims::getSubject);
        } catch (ExpiredJwtException e) {
            String refreshedToken = refreshToken(token);
            return getClaimFromToken(refreshedToken, Claims::getSubject);
        } catch (SignatureException e) {
            throw e;
        }
    }

    public Long getUserIdFromToken(String token) throws ClaimJwtException, InvalidTokenException {
        try {
            return getClaimFromToken(token, claims -> claims.get("id", Long.class));
        } catch (ExpiredJwtException e) {
            String refreshedToken = refreshToken(token);
            return getClaimFromToken(refreshedToken, claims -> claims.get("id", Long.class));
        } catch (SignatureException e) {
            throw e;
        }
    }


    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) throws ClaimJwtException, InvalidTokenException {
        try {
            final Claims claims = getAllClaimsFromToken(token);

            return claimsResolver.apply(claims);
        } catch (ClaimJwtException | SignatureException e) {
            throw e;
        }
    }

    private Claims getAllClaimsFromToken(String token) throws ClaimJwtException, InvalidTokenException {
        try {
            final Date expiration = getExpirationDateFromToken(token);
            return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
        } catch (ClaimJwtException | SignatureException e) {
            throw e;
        }
    }

    private Date getExpirationDateFromToken(String token) throws ClaimJwtException, InvalidTokenException {
        try {
            return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody().getExpiration();

        } catch (ClaimJwtException e) {
            throw new ExpiredJwtException(Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getHeader(),
                    Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody(), "Token expired");
        } catch (JwtException e) {
            throw new InvalidTokenException();
        }
    }

    public String generateToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("id", user.getId());
        claims.put("username", user.getUsername());
        claims.put("email", user.getEmail());
        claims.put("firstName", user.getFirstName());
        claims.put("secondName", user.getSecondName());
        claims.put("role", user.getRole().getRoleEnum().name());
        return doGenerateToken(claims, user.getUsername());
    }

    private String doGenerateToken(Map<String, Object> claims, String subject) {
        return Jwts.builder().setClaims(claims).setSubject(subject).setIssuedAt(new Date(System.currentTimeMillis())).setExpiration(new Date(System.currentTimeMillis() + expiration * 1000L)).signWith(SignatureAlgorithm.HS512, secret).compact();
    }

    public String refreshToken(String token) throws InvalidTokenException {
        try {
            final Claims claims = getAllClaimsFromToken(token);
            claims.setIssuedAt(new Date());
            claims.setExpiration(new Date(System.currentTimeMillis() + expiration * 1000L));
            return Jwts.builder().setClaims(claims).signWith(SignatureAlgorithm.HS512, secret).compact();

        } catch (ExpiredJwtException e) {
            final Claims claims = e.getClaims();
            claims.setIssuedAt(new Date());
            claims.setExpiration(new Date(System.currentTimeMillis() + expiration * 1000L));
            return Jwts.builder().setClaims(claims).signWith(SignatureAlgorithm.HS512, secret).compact();
        } catch (InvalidTokenException e) {
            throw e;
        }

    }
}
