package com.br.isabelaModas.config;

import com.br.isabelaModas.security.JwtUtils;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JwtConfig {

    @Value("${jwt.secret-key}")
    private String secretKey;

    @PostConstruct
    public void init() {
        JwtUtils.setSecretKey(secretKey);
    }
}
