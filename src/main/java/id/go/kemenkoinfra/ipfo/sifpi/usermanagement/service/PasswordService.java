package id.go.kemenkoinfra.ipfo.sifpi.usermanagement.service;

import java.time.LocalDateTime;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import id.go.kemenkoinfra.ipfo.sifpi.auth.model.User;
import id.go.kemenkoinfra.ipfo.sifpi.auth.repository.UserRepository;
import id.go.kemenkoinfra.ipfo.sifpi.common.exception.NotFoundException;
import id.go.kemenkoinfra.ipfo.sifpi.common.exception.SecurityException;
import id.go.kemenkoinfra.ipfo.sifpi.usermanagement.dto.SetPasswordRequest;
import id.go.kemenkoinfra.ipfo.sifpi.usermanagement.dto.UpdatePasswordRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class PasswordService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Set password for first time using token from email (for new executives).
     */
    @Transactional
    public void setPassword(SetPasswordRequest request) {
        // Validate password confirmation
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new SecurityException("Password dan konfirmasi password tidak sama");
        }

        // Find user by token
        User user = userRepository.findByPasswordSetupToken(request.getToken())
                .orElseThrow(() -> new NotFoundException("Token tidak valid atau sudah digunakan"));

        // Check if token is expired
        if (user.getPasswordSetupTokenExpiry() == null || 
            user.getPasswordSetupTokenExpiry().isBefore(LocalDateTime.now())) {
            throw new SecurityException("Token sudah kedaluwarsa. Silakan hubungi Administrator untuk mengirim ulang undangan.");
        }

        // Set password
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        
        // Clear token and activate account
        user.setPasswordSetupToken(null);
        user.setPasswordSetupTokenExpiry(null);
        user.setVerified(true);
        user.setEmailVerified(true);
        user.setActive(true);

        userRepository.save(user);
        log.info("Password set successfully for user: {}", user.getEmail());
    }

    /**
     * Update password for logged-in user (UM-9).
     */
    @Transactional
    public void updatePassword(String userEmail, UpdatePasswordRequest request) {
        // Find user
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new NotFoundException("User", userEmail));

        // Validate password confirmation
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new SecurityException("Password baru dan konfirmasi password tidak sama");
        }

        // Validate current password
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new SecurityException("Password saat ini tidak valid");
        }

        // Check if new password is same as current password
        if (passwordEncoder.matches(request.getNewPassword(), user.getPassword())) {
            throw new SecurityException("Password baru tidak boleh sama dengan password saat ini");
        }

        // Update password
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        log.info("Password updated successfully for user: {}", userEmail);
    }
}
