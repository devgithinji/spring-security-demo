package com.densoft.sec.service;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.densoft.sec.DTO.VerifyOTPReq;
import com.densoft.sec.errorhandling.APIException;
import com.densoft.sec.model.User;
import com.densoft.sec.repository.UserRepo;
import com.densoft.sec.security.SecurityUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Service
public class AuthService {

    private final UserService userService;
    private final EmailService emailService;
    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;

    private final TemplateEngine templateEngine;

    public AuthService(UserService userService, EmailService emailService, UserRepo userRepo, PasswordEncoder passwordEncoder, TemplateEngine templateEngine) {
        this.userService = userService;
        this.emailService = emailService;
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
        this.templateEngine = templateEngine;
    }

    public Map<String, String> refreshToken(HttpServletRequest request) {
        String authorizationHeader = request.getHeader(AUTHORIZATION);
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            try {
                String refresh_token = authorizationHeader.substring("Bearer ".length());
                DecodedJWT decodedJWT = SecurityUtil.decodeJwt(refresh_token);
                String username = decodedJWT.getSubject();
                User user = userService.getUser(username);
                return SecurityUtil.generateTokens(user, request.getServletPath());

            } catch (Exception e) {
                throw new APIException(e.getMessage());
            }
        } else {
            throw new APIException("Refresh token missing");
        }
    }

    public void sendOTPCode(String email) throws MessagingException, UnsupportedEncodingException {
        User user = getUser(email);
        String code = String.valueOf(new Random().nextInt(9999) + 1000);
        user.setOtpCode(passwordEncoder.encode(code));
        user.setOtpExpireTime(LocalDateTime.now().plusMinutes(2).toString());
        user.setOtpCodeVerified(false);
        userRepo.save(user);
        Map<String, Object> variables = new HashMap<>();
        variables.put("name", StringUtils.capitalize(user.getName()));
        variables.put("code", code);
        String content = templateEngine.process("otp_code.html", getThymeleafContext(variables));
        emailService.sendEmail(email, content, "OTP Code");
    }


    public void verifyOTPCode(VerifyOTPReq verifyOTPReq) {
        User user = getUser(verifyOTPReq.getEmail());
        if (user.getOtpCode() == null) throw new APIException("no OTP code found");

        if (LocalDateTime.parse(user.getOtpExpireTime()).isBefore(LocalDateTime.now()))
            throw new APIException("Expired OTP code");

        if (!passwordEncoder.matches(verifyOTPReq.getCode(), user.getOtpCode()))
            throw new APIException("Invalid OTP code");

        user.setOtpCodeVerified(true);
        userRepo.save(user);
    }

    public void passwordResetReq(String email) throws MessagingException, UnsupportedEncodingException {
        User user = getUser(email);
        String code = String.valueOf(new Random().nextInt(99999) + 10000);

        user.setPasswordResetCode(code);
        user.setPasswordResetCodeExpireTime(LocalDateTime.now().plusMinutes(5).toString());
        userRepo.save(user);

        Map<String, Object> variables = new HashMap<>();
        variables.put("name", StringUtils.capitalize(user.getName()));
        variables.put("code", code);
        String content = templateEngine.process("password_reset.html", getThymeleafContext(variables));
        emailService.sendEmail(email, content, "Password Reset Request");
    }

    public void passwordReset(String email, String password, String code) {
        User user = getUser(email);
        if (!user.getPasswordResetCode().equals(code)) throw new APIException("Invalid code");

        if(LocalDateTime.parse(user.getPasswordResetCodeExpireTime()).isBefore(LocalDateTime.now())) throw new APIException("Expired code");

        user.setPassword(passwordEncoder.encode(password));
        user.setPasswordResetCode(null);
        userRepo.save(user);
    }

    private Context getThymeleafContext(Map<String, Object> variables) {
        Context context = new Context();
        context.setVariables(variables);
        return context;
    }

    private User getUser(String email) {
        return userRepo.findUserByEmail(email).orElseThrow(() -> new APIException("No user found with email " + email));
    }
}
