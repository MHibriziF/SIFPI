package id.go.kemenkoinfra.ipfo.sifpi.auth.controller;

import id.go.kemenkoinfra.ipfo.sifpi.common.constants.AuthPath;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import id.go.kemenkoinfra.ipfo.sifpi.auth.dto.AuthResponseDTO;
import id.go.kemenkoinfra.ipfo.sifpi.auth.dto.request.LoginRequest;
import id.go.kemenkoinfra.ipfo.sifpi.auth.service.AuthService;
import id.go.kemenkoinfra.ipfo.sifpi.auth.service.AuthService.LoginResult;
import id.go.kemenkoinfra.ipfo.sifpi.common.dto.BaseResponseDTO;
import id.go.kemenkoinfra.ipfo.sifpi.common.utils.ResponseUtil;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final ResponseUtil responseUtil;

    @PostMapping(AuthPath.LOGIN)
    public ResponseEntity<BaseResponseDTO<AuthResponseDTO>> login(
            @Valid @RequestBody LoginRequest request,
            HttpServletResponse httpResponse) {

        LoginResult result = authService.login(request);
        httpResponse.addHeader(HttpHeaders.SET_COOKIE, result.cookie().toString());

        return responseUtil.success(result.data(), "Login berhasil.", HttpStatus.OK);
    }

    @GetMapping("/me")
    public ResponseEntity<BaseResponseDTO<AuthResponseDTO>> me(Authentication authentication) {
        AuthResponseDTO dto = authService.getCurrentUser(authentication.getName());
        return responseUtil.success(dto, "Berhasil.", HttpStatus.OK);
    }
}
