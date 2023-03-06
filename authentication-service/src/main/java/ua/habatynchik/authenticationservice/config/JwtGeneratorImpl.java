package ua.habatynchik.authenticationservice.config;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ua.habatynchik.authenticationservice.model.User;

import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


@Service
public class JwtGeneratorImpl implements JwtGeneratorInterface {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${app.jwttoken.message}")
    private String message;


    @Override
    public Map<String, String> generateToken(User user) {
        String jwtToken = Jwts.builder()
                .setSubject(user.getUsername())
                .setIssuedAt(Date.from(Instant.now())) //<----
                .setExpiration(Date.from(Instant.ofEpochSecond(4622470422L)))  //<----
                .signWith(SignatureAlgorithm.HS256, secret)
                .compact();

        Map<String, String> jwtTokenGen = new HashMap<>();
        jwtTokenGen.put("token", jwtToken);
        jwtTokenGen.put("message", message);
        return jwtTokenGen;
    }
}
