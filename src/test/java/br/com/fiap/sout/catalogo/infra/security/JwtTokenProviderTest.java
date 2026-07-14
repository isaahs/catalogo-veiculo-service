package br.com.fiap.sout.catalogo.infra.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
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

    @Test
    void deveGerarTokenAssinadoEmHS256EVerificavelPeloVendasService() {
        // Regressão: com jjwt/Keys.hmacShaKeyFor, secrets >= 64 bytes eram assinados
        // em HS512 (algoritmo escolhido pelo tamanho da chave), o que quebrava a
        // verificação no vendas-service, fixo em Algorithm.HMAC256 -> 403 em produção.
        String secret = "9a7262174c106437a346f906b3a0e67e3dfd36480b2713e33e9d80d2cf34d588";
        long expirationMs = 300000;

        JwtTokenProvider provider = new JwtTokenProvider(secret, expirationMs);
        String token = provider.gerarToken();

        DecodedJWT decoded = JWT.decode(token);
        assertEquals("HS256", decoded.getAlgorithm());

        Algorithm algorithm = Algorithm.HMAC256(secret);
        DecodedJWT verified = JWT.require(algorithm).build().verify(token);
        assertEquals("vehicle-catalog-service", verified.getSubject());
    }
}
