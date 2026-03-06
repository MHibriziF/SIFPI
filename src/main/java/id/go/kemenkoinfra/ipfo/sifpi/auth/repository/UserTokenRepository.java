package id.go.kemenkoinfra.ipfo.sifpi.auth.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import id.go.kemenkoinfra.ipfo.sifpi.auth.model.UserToken;
import id.go.kemenkoinfra.ipfo.sifpi.common.enums.TokenType;

public interface UserTokenRepository extends JpaRepository<UserToken, UUID> {
    Optional<UserToken> findByToken(String token);
    Optional<UserToken> findByTokenAndType(String token, TokenType type);
}
