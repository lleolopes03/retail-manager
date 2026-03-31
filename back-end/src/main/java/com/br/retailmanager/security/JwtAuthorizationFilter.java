package com.br.retailmanager.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JwtAuthorizationFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(JwtAuthorizationFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        // 🔹 Lê o header cru
        String header = request.getHeader("Authorization");
        log.info("Authorization header recebido: {}", header);

        if (header == null || !header.startsWith("Bearer ")) {
            log.warn("Authorization header ausente ou inválido");
            filterChain.doFilter(request, response);
            return;
        }

        // 🔹 Extrai apenas o token cru (sem "Bearer ")
        String token = header.substring(7);

        if (!JwtUtils.isTokenValid(token)) {
            log.warn("Token inválido: {}", token);
            filterChain.doFilter(request, response);
            return;
        }

        Claims claims = Jwts.parserBuilder()
                .setSigningKey(JwtUtils.generateKey())
                .build()
                .parseClaimsJws(token)
                .getBody();

        String username = claims.getSubject();
        String role = claims.get("role", String.class);

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            List<SimpleGrantedAuthority> authorities =
                    List.of(new SimpleGrantedAuthority("ROLE_" + role));

            UserDetails userDetails = new org.springframework.security.core.userdetails.User(
                    username,
                    "",
                    authorities
            );

            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(userDetails, null, authorities);

            authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);

            log.info("Username extraído: {}", username);
            log.info("Role extraída: {}", role);
            log.info("Authorities injetadas: {}", authorities);
        }

        filterChain.doFilter(request, response);
    }
}