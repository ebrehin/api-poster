package com.api.security;

import com.api.dto.ErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;

@WebFilter(urlPatterns = "/api/*")
public class JwtAuthFilter implements Filter {
    private static final String AUTH_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    private ObjectMapper mapper;
    private JwtParser parser;
    private Duration fallbackExpiration;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        try {
            JwtConfig config = JwtConfig.load();
            SecretKey key = Keys.hmacShaKeyFor(config.secret().getBytes(StandardCharsets.UTF_8));
            parser = Jwts.parser().verifyWith(key).build();
            fallbackExpiration = config.expiration();
            mapper = new ObjectMapper();
        } catch (RuntimeException e) {
            throw new ServletException("Erreur d'initialisation JWT", e);
        }
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        if (!(request instanceof HttpServletRequest) || !(response instanceof HttpServletResponse)) {
            chain.doFilter(request, response);
            return;
        }

        HttpServletRequest httpReq = (HttpServletRequest) request;
        HttpServletResponse httpRes = (HttpServletResponse) response;

        if ("OPTIONS".equalsIgnoreCase(httpReq.getMethod())) {
            chain.doFilter(request, response);
            return;
        }

        String auth = httpReq.getHeader(AUTH_HEADER);
        if (auth == null || !auth.startsWith(BEARER_PREFIX)) {
            sendError(httpRes, HttpServletResponse.SC_UNAUTHORIZED, "Token manquant ou invalide.");
            return;
        }

        String token = auth.substring(BEARER_PREFIX.length()).trim();
        if (token.isEmpty()) {
            sendError(httpRes, HttpServletResponse.SC_UNAUTHORIZED, "Token manquant ou invalide.");
            return;
        }

        try {
            Jws<Claims> jws = parser.parseSignedClaims(token);
            Claims claims = jws.getPayload();

            Date exp = claims.getExpiration();
            if (exp == null) {
                Date iat = claims.getIssuedAt();
                if (iat == null) {
                    sendError(httpRes, HttpServletResponse.SC_UNAUTHORIZED, "Token sans expiration.");
                    return;
                }
                Instant expiresAt = iat.toInstant().plus(fallbackExpiration);
                if (Instant.now().isAfter(expiresAt)) {
                    sendError(httpRes, HttpServletResponse.SC_UNAUTHORIZED, "Token expiré.");
                    return;
                }
            }

            httpReq.setAttribute("jwtClaims", claims);
            chain.doFilter(request, response);
        } catch (JwtException e) {
            sendError(httpRes, HttpServletResponse.SC_UNAUTHORIZED, "Token invalide.");
        }
    }

    private void sendError(HttpServletResponse response, int status, String message) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json;charset=UTF-8");
        mapper.writeValue(response.getWriter(), new ErrorResponse(status, message));
    }
}
