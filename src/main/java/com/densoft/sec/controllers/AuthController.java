package com.densoft.sec.controllers;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.densoft.sec.errorhandling.APIException;
import com.densoft.sec.model.User;
import com.densoft.sec.repository.UserRepo;
import com.densoft.sec.security.SecurityUtil;
import com.densoft.sec.service.AuthService;
import com.densoft.sec.service.EmailService;
import com.densoft.sec.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Random;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping("/api")
@Slf4j
public class AuthController {

    private final AuthService authService;


    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping("/refresh_token")
    public ResponseEntity<?> refreshToken(HttpServletRequest request) throws IOException {
        Map<String, String> tokens = authService.refreshToken(request);
        return new ResponseEntity<>(tokens, HttpStatus.OK);
    }

    @PostMapping("/otp/email/{email}")
    public ResponseEntity<?> sendOTPCode(@PathVariable("email") String email) throws MessagingException, UnsupportedEncodingException {
        authService.sendOTPCode(email);
        return new ResponseEntity<>(Map.of("message", "check your email"), HttpStatus.OK);
    }

    @PostMapping("/email/{email_id}/code/{code}")
    public ResponseEntity<Object> verify2faCode(@PathVariable("email_id") String email, @PathVariable("code") String code) {
//        User user = userRepo.findUserByEmailAndCode(email, code).orElseThrow(() -> new RuntimeException("invalid code"));
        return null;
    }
}
