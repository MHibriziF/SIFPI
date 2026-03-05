package id.go.kemenkoinfra.ipfo.sifpi.auth.service.impl;

import id.go.kemenkoinfra.ipfo.sifpi.auth.model.User;
import id.go.kemenkoinfra.ipfo.sifpi.auth.repository.UserRepository;
import id.go.kemenkoinfra.ipfo.sifpi.auth.service.EmailVerificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailVerificationServiceImpl implements EmailVerificationService {

    private final UserRepository userRepository;

    @Override
    @Transactional
    public void verifyEmail(String token) {
        if (token == null || token.trim().isEmpty()) {
            throw new IllegalArgumentException("Token verifikasi tidak valid");
        }

        User user = userRepository.findByEmailVerificationToken(token)
                .orElseThrow(() -> new IllegalArgumentException("Token verifikasi tidak ditemukan atau sudah digunakan"));

        // Check if token is expired
        if (user.getEmailVerificationTokenExpiry() == null || LocalDateTime.now().isAfter(user.getEmailVerificationTokenExpiry())) {
            throw new IllegalArgumentException("Token verifikasi telah kedaluwarsa");
        }

        // Mark email as verified
        user.setEmailVerified(true);
        user.setEmailVerificationToken(null);
        user.setEmailVerificationTokenExpiry(null);

        userRepository.save(user);
        log.info("Email verified successfully for user: {}", user.getEmail());
    }

    @Override
    @Transactional
    public void resendVerificationEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email tidak valid");
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User dengan email ini tidak ditemukan"));

        // Check if already verified
        if (user.isEmailVerified()) {
            throw new IllegalArgumentException("Email Anda sudah terverifikasi");
        }

        // ⚠️ DEPRECATED: Resend is no longer used in current flow
        // Token expiry is a hard deadline - users must register again
        // This method is kept for backward compatibility only
        throw new IllegalArgumentException(
            "Jika token Anda sudah expired, silakan daftar ulang dari awal."
        );
    }
}
