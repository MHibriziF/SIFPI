package id.go.kemenkoinfra.ipfo.sifpi.auth.service.impl;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.stereotype.Service;

import id.go.kemenkoinfra.ipfo.sifpi.auth.model.User;
import id.go.kemenkoinfra.ipfo.sifpi.auth.model.UserToken;
import id.go.kemenkoinfra.ipfo.sifpi.auth.repository.UserTokenRepository;
import id.go.kemenkoinfra.ipfo.sifpi.auth.service.UserTokenService;
import id.go.kemenkoinfra.ipfo.sifpi.common.enums.TokenType;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserTokenServiceImpl implements UserTokenService {

    private final UserTokenRepository userTokenRepository;

    @Override
    public String createToken(User user, TokenType type, LocalDateTime expiresAt) {
        String token = UUID.randomUUID().toString().replace("-", "");

        UserToken userToken = new UserToken();
        userToken.setUser(user);
        userToken.setToken(token);
        userToken.setType(type);
        userToken.setExpiresAt(expiresAt);

        userTokenRepository.save(userToken);
        return token;
    }
}
