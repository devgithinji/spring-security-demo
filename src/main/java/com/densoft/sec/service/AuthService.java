package com.densoft.sec.service;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.densoft.sec.errorhandling.APIException;
import com.densoft.sec.model.User;
import com.densoft.sec.repository.UserRepo;
import com.densoft.sec.security.SecurityUtil;
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

    private final TemplateEngine templateEngine;

    public AuthService(UserService userService, EmailService emailService, UserRepo userRepo, TemplateEngine templateEngine) {
        this.userService = userService;
        this.emailService = emailService;
        this.userRepo = userRepo;
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
        User user = userRepo.findUserByEmail(email).orElseThrow(() -> new APIException("No user found with email " + email));
        String code = String.valueOf(new Random().nextInt(9999) + 1000);
        user.setCode(code);
        user.setExpire_time(LocalDateTime.now().plusMinutes(2).toString());
        userRepo.save(user);
        Context context = new Context();
        Map<String, Object> variables = new HashMap<>();
        variables.put("name", StringUtils.capitalize(user.getName()));
        variables.put("code", code);
        context.setVariables(variables);
        String content = templateEngine.process("otp_code.html", context);
        emailService.sendEmail(email, content, "OTP Code");
    }

    public boolean verifyOTPCode() {
        return false;
    }
}
