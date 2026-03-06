package id.go.kemenkoinfra.ipfo.sifpi.auth.service;

import java.time.LocalDateTime;

import id.go.kemenkoinfra.ipfo.sifpi.auth.model.User;
import id.go.kemenkoinfra.ipfo.sifpi.common.enums.TokenType;

public interface UserTokenService {

    /**
     * Generate, persist, and return a token for the given user.
     */
    String createToken(User user, TokenType type, LocalDateTime expiresAt);
}
