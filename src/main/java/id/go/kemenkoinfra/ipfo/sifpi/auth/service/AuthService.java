package id.go.kemenkoinfra.ipfo.sifpi.auth.service;

import org.springframework.http.ResponseCookie;

import id.go.kemenkoinfra.ipfo.sifpi.auth.dto.AuthResponseDTO;
import id.go.kemenkoinfra.ipfo.sifpi.auth.dto.LoginRequest;

public interface AuthService {

    LoginResult login(LoginRequest request);

    AuthResponseDTO getCurrentUser(String email);

    record LoginResult(ResponseCookie cookie, AuthResponseDTO data) {}
}
