package id.go.kemenkoinfra.ipfo.sifpi.auth.service.impl;

import java.util.List;
import java.util.Map;

import id.go.kemenkoinfra.ipfo.sifpi.auth.service.AuthService;
import org.springframework.http.ResponseCookie;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import id.go.kemenkoinfra.ipfo.sifpi.auth.dto.AuthResponseDTO;
import id.go.kemenkoinfra.ipfo.sifpi.auth.dto.request.LoginRequest;
import id.go.kemenkoinfra.ipfo.sifpi.auth.mapper.AuthMapper;
import id.go.kemenkoinfra.ipfo.sifpi.auth.model.RolePermission;
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
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final RolePermissionRepository rolePermissionRepository;
    private final AuthMapper authMapper;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional(readOnly = true)
    public LoginResult login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new SecurityException("Email atau password salah."));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new SecurityException("Email atau password salah.");
        }

        // Check if account is active
        if (!user.isActive()) {
            throw new SecurityException("Akun Anda telah dinonaktifkan. Hubungi administrator untuk informasi lebih lanjut.");
        }

        // NEW: Check if email is verified
        if (!user.isEmailVerified()) {
            throw new SecurityException("Email Anda belum diverifikasi. Silakan cek email Anda untuk link verifikasi.");
        }

        List<RolePermission> rolePermissions = rolePermissionRepository.findByRoleWithResource(user.getRole());
        AuthResponseDTO dto = authMapper.toDTO(user, rolePermissions);

        String token = jwtService.generateToken(user.getEmail(), Map.of(
                "role", user.getRole().getName(),
                "permissions", dto.getPermissions()
        ));

        log.info("User logged in: {}", user.getEmail());

        return new LoginResult(jwtService.createJwtCookie(token), dto);
    }

    @Override
    public ResponseCookie logout() {
        return jwtService.createExpiredJwtCookie();
    }

    @Override
    @Transactional(readOnly = true)
    public AuthResponseDTO getCurrentUser(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new SecurityException("Pengguna tidak ditemukan."));

        List<RolePermission> rolePermissions = rolePermissionRepository.findByRoleWithResource(user.getRole());
        return authMapper.toDTO(user, rolePermissions);
    }
}
