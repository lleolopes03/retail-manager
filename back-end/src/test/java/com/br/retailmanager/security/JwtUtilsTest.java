package com.br.retailmanager.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilsTest {

    @BeforeEach
    void setUp() {
        JwtUtils.setSecretKey("minhaChaveSuperSecretaDeSeguranca1234567890");
    }

    @Test
    void deveCriarTokenValido() {
        String token = JwtUtils.createToken("ana@email.com", "ADMIN", 1L);

        assertNotNull(token);
        assertFalse(token.isBlank());
        assertTrue(token.split("\\.").length == 3);
    }

    @Test
    void deveExtrairUsernameDoToken() {
        String token = JwtUtils.createToken("ana@email.com", "ADMIN", 1L);

        String username = JwtUtils.getUsernameFromToken(token);

        assertEquals("ana@email.com", username);
    }

    @Test
    void deveExtrairUserIdDoToken() {
        String token = JwtUtils.createToken("ana@email.com", "VENDEDOR", 42L);

        Long userId = JwtUtils.getUserIdFromToken(token);

        assertEquals(42L, userId);
    }

    @Test
    void deveValidarTokenValido() {
        String token = JwtUtils.createToken("ana@email.com", "GERENTE_SISTEMA", 1L);

        assertTrue(JwtUtils.isTokenValid(token));
    }

    @Test
    void deveRetornarFalsoParaTokenInvalido() {
        assertFalse(JwtUtils.isTokenValid("token.invalido.qualquer"));
    }

    @Test
    void deveRetornarFalsoParaTokenVazio() {
        assertFalse(JwtUtils.isTokenValid(""));
    }

    @Test
    void deveRetornarFalsoParaTokenNull() {
        assertFalse(JwtUtils.isTokenValid(null));
    }

    @Test
    void deveCriarRefreshToken() {
        String refreshToken = JwtUtils.createRefreshToken("ana@email.com");

        assertNotNull(refreshToken);
        assertTrue(JwtUtils.isTokenValid(refreshToken));
        assertEquals("ana@email.com", JwtUtils.getUsernameFromToken(refreshToken));
    }

    @Test
    void deveRetornarFalsoParaTokenAssinadoComChaveDiferente() {
        JwtUtils.setSecretKey("chaveOriginalDeSegurancaSuperLonga1234567890");
        String token = JwtUtils.createToken("ana@email.com", "ADMIN", 1L);

        JwtUtils.setSecretKey("outraChaveCompletamenteDiferente1234567890XX");
        assertFalse(JwtUtils.isTokenValid(token));

        JwtUtils.setSecretKey("minhaChaveSuperSecretaDeSeguranca1234567890");
    }

    @Test
    void deveCriarTokensDiferentesParaUsuariosDiferentes() {
        String tokenAna = JwtUtils.createToken("ana@email.com", "ADMIN", 1L);
        String tokenMaria = JwtUtils.createToken("maria@email.com", "VENDEDOR", 2L);

        assertNotEquals(tokenAna, tokenMaria);
    }
}
