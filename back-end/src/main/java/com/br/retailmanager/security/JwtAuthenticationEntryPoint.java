package com.br.retailmanager.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import org.springframework.security.web.AuthenticationEntryPoint;

public class JwtAuthenticationEntryPoint  implements AuthenticationEntryPoint {
    private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationEntryPoint.class);

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, org.springframework.security.core.AuthenticationException authenticationException) throws java.io.IOException, ServletException {

        response.setHeader("www-authenticate","Bearer realm='/api/v1/auth'");
        response.sendError(401);
    }
}
