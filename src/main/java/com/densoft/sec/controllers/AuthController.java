package com.densoft.sec.controllers;

import com.densoft.sec.DTO.ResetPasswordReq;
import com.densoft.sec.DTO.VerifyOTPReq;
import com.densoft.sec.service.AuthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Map;

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

    @PostMapping("/otp/verify")
    public ResponseEntity<?> verifyOTPCode(@RequestBody VerifyOTPReq otpReq) {
        authService.verifyOTPCode(otpReq);
        return new ResponseEntity<>(Map.of("message", "OTP code verified"), HttpStatus.OK);
    }

    @PostMapping("/password-reset/request/{email}")
    public ResponseEntity<?> passwordResetRequest(@PathVariable("email") String email) throws MessagingException, UnsupportedEncodingException {
        authService.passwordResetReq(email);
        return new ResponseEntity<>(Map.of("message", "Password reset code sent to email"), HttpStatus.OK);
    }

    @PostMapping("/password-reset")
    public ResponseEntity<?> passwordResetRequest(@RequestBody ResetPasswordReq resetPasswordReq) {
        authService.passwordReset(resetPasswordReq.getEmail(), resetPasswordReq.getPassword(), resetPasswordReq.getCode());
        return new ResponseEntity<>(Map.of("message", "Password reset successfully"), HttpStatus.OK);
    }
}
