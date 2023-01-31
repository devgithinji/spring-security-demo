package com.densoft.sec.security.filters;

import com.densoft.sec.model.User;
import com.densoft.sec.security.SecurityUtil;
import com.densoft.sec.service.AuthService;
import com.densoft.sec.service.UserServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.mail.MessagingException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

public class CustomAuthFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthService authService;

    private final AuthenticationManager authenticationManager;

    public CustomAuthFilter(AuthService authService, AuthenticationManager authenticationManager) {
        this.authService = authService;
        this.authenticationManager = authenticationManager;
    }

    @SneakyThrows
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        ObjectMapper objectMapper = new ObjectMapper();
        var req = objectMapper.readValue(request.getInputStream(), Map.class);
        if (req.get("email") == null || req.get("password") == null) {
            SecurityUtil.sendResponse(response, "password / email required");
        }
        String email = req.get("email").toString();
        String password = req.get("password").toString();
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(email, password);
        return authenticationManager.authenticate(authenticationToken);

    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        User user = (User) authResult.getPrincipal();
        try {
            authService.sendOTPCode(user.getEmail());
            response.setContentType(APPLICATION_JSON_VALUE);
            Map<String, String> body = SecurityUtil.generateTokens((User) authResult.getPrincipal(), request.getServletPath());
            body.put("message", "OTP code sent to your email");
            new ObjectMapper().writeValue(response.getOutputStream(), body);
        } catch (MessagingException e) {
            SecurityUtil.sendResponse(response, "something went wrong");
        }
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
        SecurityUtil.sendResponse(response, failed.getMessage());
    }


}
