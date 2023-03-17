package ua.habatynchik.webclient.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Controller
@Log4j2
public class WebController {
    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/profile")
    public String showProfile(@SessionAttribute("token") String token, Model model) {
        // используем токен для выполнения запросов к защищенным ресурсам

        log.info(token);
        return "redirect:";
    }

    @PostMapping("/login")
    public String doLogin(@RequestParam("login") String login, @RequestParam("password") String password, HttpSession session, HttpServletResponse response) {

        RestTemplate restTemplate = new RestTemplate();
        String url = "http://localhost:8081/api/auth/login";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("login", login);
        requestBody.put("password", password);

        // создаем HttpEntity с телом запроса и заголовками
        HttpEntity<Map<String, String>> request = new HttpEntity<>(requestBody, headers);

        ResponseEntity<String> restResponse  = restTemplate.postForEntity( url, request , String.class );

        if (restResponse.getStatusCode() == HttpStatus.OK) {
            String token = restResponse.getHeaders().getFirst(HttpHeaders.AUTHORIZATION).substring(7);
            Cookie cookie = new Cookie("token", token);
            cookie.setMaxAge(24 * 60 * 60);
            cookie.setPath("/");
            response.addCookie(cookie);
        }

        return "redirect:";
    }

    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("userDto", new UserRegistrationDto());

        return "register";
    }

    @PostMapping("/register")
    public String doRegister(

    ) {
        return "redirect:/login";
    }
}
