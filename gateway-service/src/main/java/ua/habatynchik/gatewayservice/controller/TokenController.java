package ua.habatynchik.gatewayservice.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ua.habatynchik.gatewayservice.dto.ValidateTokenResponseDto;
import ua.habatynchik.gatewayservice.service.TokenService;

@RestController
@RequestMapping("api/token")
@AllArgsConstructor
public class TokenController {

    private final TokenService tokenService;

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshJWT(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader) {
        String token = takeOutJwtToken(authorizationHeader);

        if (token == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }


        String result = tokenService.refreshToken(token);

        if (result.equals("Error: Invalid JWT token")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(result);
        }

        return ResponseEntity.ok()
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + result)
                .body(result);
    }


    @PostMapping("/validate")
    public ResponseEntity<?> validateJWT(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader) {
        String token = takeOutJwtToken(authorizationHeader);

        if (token == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        String result = tokenService.validateToken(token);

        switch (result) {
            case "Invalid JWT token", "User not found", "Token expired" -> {
                ValidateTokenResponseDto response = ValidateTokenResponseDto.builder()
                        .message(result)
                        .build();
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
            case "Unexpected error" -> {
                ValidateTokenResponseDto response = ValidateTokenResponseDto.builder()
                        .message(result)
                        .build();
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
            }
        }

        ValidateTokenResponseDto response = ValidateTokenResponseDto.builder()
                .userId(Long.valueOf(result))
                .message("Token is valid")
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .body(response);
    }

    private String takeOutJwtToken(String authorizationHeader) {
        if (!authorizationHeader.startsWith("Bearer ")) {
            return null;
        }

        return authorizationHeader.substring(7);
    }

}
