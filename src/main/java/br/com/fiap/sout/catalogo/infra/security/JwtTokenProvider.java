package br.com.fiap.sout.catalogo.infra.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class JwtTokenProvider {

    private final Algorithm algorithm;
    private final long expirationMs;

    public JwtTokenProvider(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.expiration-ms}") long expirationMs) {
        this.algorithm = Algorithm.HMAC256(secret);
        this.expirationMs = expirationMs;
    }

    public String gerarToken() {
        return JWT.create()
                .withSubject("vehicle-catalog-service")
                .withIssuedAt(java.util.Date.from(Instant.now()))
                .withExpiresAt(java.util.Date.from(Instant.now().plusMillis(expirationMs)))
                .sign(algorithm);
    }
}