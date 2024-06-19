package dev.easylogbe.common.util;

import dev.easylogbe.common.exception.ErrorCode;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Map;
import javax.crypto.SecretKey;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtProvider {

    @Value("${spring.jwt.secret}")
    private String secretKey;

    @Value("${spring.jwt.issuer}")
    private String issuer;

    @Value("${spring.jwt.access-token-expiration-minutes}")
    private int accessTokenExpirationMinutes;

    @Value("${spring.jwt.refresh-token-expiration-minutes}")
    private int refreshTokenExpirationMinutes;

    public String createJwt(Long userId, boolean isAccessToken) {
        Instant now = Instant.now();
        Map<String, Object> claims = createClaims(userId);
        int tokenExpirationMinutes = getTokenExpirationMinutes(isAccessToken);
        return Jwts.builder()
                .issuer(issuer)
                .subject(String.valueOf(userId))
                .claims(claims)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plus(tokenExpirationMinutes, ChronoUnit.MINUTES)))
                .signWith(getSecretKey())
                .compact();
    }

    private int getTokenExpirationMinutes(boolean isAccessToken) {
        return isAccessToken ? accessTokenExpirationMinutes : refreshTokenExpirationMinutes;
    }

    public Claims getClaims(String accessToken) {
        try {
            return Jwts.parser()
                    .verifyWith(getSecretKey())
                    .build()
                    .parseSignedClaims(accessToken)
                    .getPayload();
        } catch (SecurityException | MalformedJwtException e) {
            throw new JwtException(ErrorCode.JWT_INVALID_SIGNATURE.getMessage());
        } catch (ExpiredJwtException | UnsupportedJwtException e) {
            throw new JwtException(ErrorCode.JWT_EXPIRED.getMessage());
        }
    }

    private SecretKey getSecretKey() {
        return Keys.hmacShaKeyFor(this.secretKey.getBytes(StandardCharsets.UTF_8));
    }

    private Map<String, Object> createClaims(Long userId) {
        return Map.of(
                "id", userId
        );
    }
}
