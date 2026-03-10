package id.go.kemenkoinfra.ipfo.sifpi.usermanagement.service.impl;

import java.time.LocalDateTime;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import id.go.kemenkoinfra.ipfo.sifpi.auth.model.User;
import id.go.kemenkoinfra.ipfo.sifpi.auth.model.UserToken;
import id.go.kemenkoinfra.ipfo.sifpi.auth.repository.UserRepository;
import id.go.kemenkoinfra.ipfo.sifpi.auth.repository.UserTokenRepository;
import id.go.kemenkoinfra.ipfo.sifpi.common.enums.TokenType;
import id.go.kemenkoinfra.ipfo.sifpi.common.exception.BadRequestException;
import id.go.kemenkoinfra.ipfo.sifpi.common.exception.NotFoundException;
import id.go.kemenkoinfra.ipfo.sifpi.common.exception.SecurityException;
import id.go.kemenkoinfra.ipfo.sifpi.usermanagement.dto.request.SetPasswordRequest;
import id.go.kemenkoinfra.ipfo.sifpi.usermanagement.dto.request.UpdatePasswordRequest;
import id.go.kemenkoinfra.ipfo.sifpi.usermanagement.service.PasswordService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class PasswordServiceImpl implements PasswordService {

    private final UserRepository userRepository;
    private final UserTokenRepository userTokenRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Set password for first time using token from email (for new executives).
     */
    @Override
    @Transactional
    public void setPassword(SetPasswordRequest request) {
        // Validate password confirmation
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new SecurityException("Password dan konfirmasi password tidak sama");
        }

        // Find token
        UserToken userToken = userTokenRepository.findByTokenAndType(request.getToken(), TokenType.PASSWORD_SETUP)
                .orElseThrow(() -> new NotFoundException("Token tidak valid atau sudah digunakan"));

        // Check if token is expired
        if (userToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new SecurityException("Token sudah kedaluwarsa. Silakan hubungi Administrator untuk mengirim ulang undangan.");
        }

        // Set password and activate account
        User user = userToken.getUser();
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setEmailVerified(true);
        user.setActive(true);
        userRepository.save(user);
        userTokenRepository.delete(userToken);
        log.info("Password set successfully for user: {}", user.getEmail());
    }

    /**
     * Update password for logged-in user (UM-9).
     */
    @Override
    @Transactional
    public void updatePassword(String userEmail, UpdatePasswordRequest request) {
        // Find user
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new NotFoundException("User", userEmail));

        // Validate password confirmation
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new BadRequestException("Password baru dan konfirmasi password tidak sama");
        }

        // Validate current password
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new BadRequestException("Password saat ini salah");
        }

        // Check if new password is same as current password
        if (passwordEncoder.matches(request.getNewPassword(), user.getPassword())) {
            throw new BadRequestException("Password baru tidak boleh sama dengan password lama");
        }

        // Update password
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        log.info("Password updated successfully for user: {}", userEmail);
    }
}
