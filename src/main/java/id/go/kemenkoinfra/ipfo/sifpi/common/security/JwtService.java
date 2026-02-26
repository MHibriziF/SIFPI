package id.go.kemenkoinfra.ipfo.sifpi.common.security;

import java.util.Map;

import org.springframework.http.ResponseCookie;

import io.jsonwebtoken.Claims;

public interface JwtService {

    String generateToken(String subject, Map<String, Object> extraClaims);

    Claims extractClaimsOrNull(String token);

    Claims extractAllClaims(String token);

    ResponseCookie createJwtCookie(String token);

    ResponseCookie createExpiredJwtCookie();
}
