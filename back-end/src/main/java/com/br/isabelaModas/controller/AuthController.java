package com.br.isabelaModas.controller;

import com.br.isabelaModas.dtos.JwtResponse;
import com.br.isabelaModas.dtos.LoginRequest;
import com.br.isabelaModas.security.FuncionarioDetailsService;
import com.br.isabelaModas.security.JwtToken;
import com.br.isabelaModas.security.JwtUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;


@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final FuncionarioDetailsService detailsService;


    public AuthController(AuthenticationManager authenticationManager, FuncionarioDetailsService detailsService) {
        this.authenticationManager = authenticationManager;
        this.detailsService = detailsService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        try {
            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(loginRequest.getLogin(), loginRequest.getSenha());

            authenticationManager.authenticate(authenticationToken);

            // gera access token
            String jwt = detailsService.getTokenAuthenticated(loginRequest.getLogin());

            // gera refresh token (expiração maior)
            String refreshToken = JwtUtils.createRefreshToken(loginRequest.getLogin());

            return ResponseEntity.ok(new JwtResponse(jwt, refreshToken));
        } catch (AuthenticationException ex) {
            return ResponseEntity.badRequest().body("Credenciais inválidas");
        }
    }
    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@RequestBody Map<String, String> body) {
        String refresh = body.get("refresh");
        if (JwtUtils.isTokenValid(refresh)) {
            String newToken = detailsService.getTokenAuthenticated(JwtUtils.getUsernameFromToken(refresh));
            return ResponseEntity.ok(new JwtResponse(newToken, refresh));
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Refresh token inválido");
        }
    }


}