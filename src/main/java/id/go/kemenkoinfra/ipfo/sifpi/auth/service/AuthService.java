package id.go.kemenkoinfra.ipfo.sifpi.auth.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.ResponseCookie;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import id.go.kemenkoinfra.ipfo.sifpi.auth.dto.AuthResponseDTO;
import id.go.kemenkoinfra.ipfo.sifpi.auth.dto.LoginRequest;
import id.go.kemenkoinfra.ipfo.sifpi.auth.mapper.AuthMapper;
import id.go.kemenkoinfra.ipfo.sifpi.auth.model.User;
import id.go.kemenkoinfra.ipfo.sifpi.auth.repository.RolePermissionRepository;
import id.go.kemenkoinfra.ipfo.sifpi.auth.repository.UserRepository;
import id.go.kemenkoinfra.ipfo.sifpi.common.exception.SecurityException;
import id.go.kemenkoinfra.ipfo.sifpi.common.security.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RolePermissionRepository rolePermissionRepository;
    private final AuthMapper authMapper;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public LoginResult login(LoginRequest request) {
        User user = userRepository.findByEmailAndDeletedAtIsNull(request.getEmail())
                .orElseThrow(() -> new SecurityException("Email atau password salah."));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new SecurityException("Email atau password salah.");
        }

        Map<String, List<String>> permissions = buildPermissions(user);
        String token = jwtService.generateToken(user.getEmail(), Map.of(
                "role", user.getRole().getName(),
                "permissions", permissions
        ));

        log.info("User logged in: {}", user.getEmail());

        AuthResponseDTO dto = authMapper.toDTO(user);
        dto.setPermissions(permissions);

        return new LoginResult(jwtService.createJwtCookie(token), dto);
    }

    @Transactional(readOnly = true)
    public AuthResponseDTO getCurrentUser(String email) {
        User user = userRepository.findByEmailAndDeletedAtIsNull(email)
                .orElseThrow(() -> new SecurityException("Pengguna tidak ditemukan."));

        AuthResponseDTO dto = authMapper.toDTO(user);
        dto.setPermissions(buildPermissions(user));

        return dto;
    }

    private Map<String, List<String>> buildPermissions(User user) {
        return rolePermissionRepository.findByRoleWithResource(user.getRole())
                .stream()
                .collect(Collectors.groupingBy(
                        rp -> rp.getAction().name(),
                        Collectors.mapping(rp -> rp.getResource().getName(), Collectors.toList())
                ));
    }

    public record LoginResult(ResponseCookie cookie, AuthResponseDTO data) {}
}
