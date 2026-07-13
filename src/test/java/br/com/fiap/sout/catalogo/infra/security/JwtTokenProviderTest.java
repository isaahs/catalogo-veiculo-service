package br.com.fiap.sout.catalogo.infra.security;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JwtTokenProviderTest {

    @Test
    void deveGerarTokenValido() {
        // Secret must be at least 256 bits (32 bytes) for HMAC-SHA256
        String secret = "um-segredo-super-secreto-de-32-bytes-pelo-menos";
        long expirationMs = 3600000;
        
        JwtTokenProvider provider = new JwtTokenProvider(secret, expirationMs);
        String token = provider.gerarToken();
        
        assertNotNull(token);
        assertFalse(token.isEmpty());
    }
}
