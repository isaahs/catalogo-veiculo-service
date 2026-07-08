package br.com.fiap.sout.catalogo.infra.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;

@Component
public class JwtTokenProvider {

    private final SecretKey secretKey;
    private final long expirationMs;

    public JwtTokenProvider(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.expiration-ms}") long expirationMs) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expirationMs = expirationMs;
    }

    public String gerarToken() {
        Instant agora = Instant.now();
        Instant dataExpiracao = agora.plusMillis(expirationMs);

        return Jwts.builder()
                .subject("vehicle-catalog-service")
                .issuedAt(Date.from(agora))
                .expiration(Date.from(dataExpiracao))
                .signWith(secretKey)
                .compact();
    }
}
