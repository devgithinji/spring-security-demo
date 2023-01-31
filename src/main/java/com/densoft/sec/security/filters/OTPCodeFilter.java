package com.densoft.sec.security.filters;

import com.densoft.sec.model.User;
import com.densoft.sec.security.SecurityUtil;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class OTPCodeFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (!request.getServletPath().startsWith("/api/otp/") && !request.getServletPath().startsWith("/api/password-reset")) {

            if (SecurityContextHolder.getContext().getAuthentication() != null) {
                User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
                if (!user.isOtpCodeVerified()) {
                    SecurityUtil.sendResponse(response, "verify otp code please");
                }
            }

        }
        filterChain.doFilter(request, response);
    }
}
