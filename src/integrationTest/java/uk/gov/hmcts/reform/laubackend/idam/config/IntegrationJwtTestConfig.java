package uk.gov.hmcts.reform.laubackend.idam.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;

import java.time.Instant;

@TestConfiguration
public class IntegrationJwtTestConfig {

    @Bean
    public JwtDecoder jwtDecoder() {
        return token -> {
            if (token == null || token.isBlank()) {
                throw new JwtException("Missing token");
            }
            final Instant now = Instant.now();
            return Jwt.withTokenValue(token)
                .header("alg", "none")
                .subject("integration-test-user")
                .issuedAt(now.minusSeconds(60))
                .expiresAt(now.plusSeconds(3600))
                .claim("scope", "openid profile roles")
                .build();
        };
    }
}
