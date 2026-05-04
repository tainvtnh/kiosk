package com.tnh.kiosk.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.tnh.kiosk.exceptions.BWCGenericRuntimeException;
import com.tnh.kiosk.services.MessageService;
import com.tnh.kiosk.utils.LogStyleHelper;
import com.tnh.kiosk.components.BasewareCoreFilter;


import java.util.Collections;

@Slf4j
@Component
public class JwtAuthFilter extends BasewareCoreFilter {

    MessageService messageService;
    ObjectMapper objectMapper;
    AntPathMatcher pathMatcher = new AntPathMatcher();

    private JwtUtil jwtUtil;

    public JwtAuthFilter(MessageService messageService, ObjectMapper objectMapper, JwtUtil jwtUtil) {
        super(messageService, objectMapper);
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        final String header = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);

            try {
                // Kiểm tra token hợp lệ
                if (jwtUtil.validateToken(token)) {
                    String username = jwtUtil.getUsernameFromToken(token);
                    UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                            new User(username, "", Collections.emptyList()), null, Collections.emptyList());
                    auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(auth);
                }

            } catch (ExpiredJwtException e) {
                // Token hết hạn
                log.error(LogStyleHelper.error("JWT expired: {}"), e.getMessage(), e);
                sendErrorResponse(response, HttpServletResponse.SC_FORBIDDEN, "error.token.expired");
                return;

            } catch (JwtException e) {
                // Token không hợp lệ (signature sai, parse lỗi, v.v.)
                log.error(LogStyleHelper.error("Invalid JWT: {}"), e.getMessage(), e);
                sendErrorResponse(response, HttpServletResponse.SC_FORBIDDEN, "error.invalid.token");
                return;

            } catch (Exception e) {
                // Các lỗi khác trong quá trình xử lý
                log.error(LogStyleHelper.error("Error writing error response: {}"), e.getMessage(), e);
                throw new BWCGenericRuntimeException("Error writing error response", e);
            }
        }

        chain.doFilter(request, response);
    }
}