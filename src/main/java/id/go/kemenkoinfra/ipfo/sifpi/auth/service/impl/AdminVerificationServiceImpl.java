package id.go.kemenkoinfra.ipfo.sifpi.auth.service.impl;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import id.go.kemenkoinfra.ipfo.sifpi.auth.dto.VerificationResponseDTO;
import id.go.kemenkoinfra.ipfo.sifpi.auth.dto.request.VerifyOwnerRequest;
import id.go.kemenkoinfra.ipfo.sifpi.auth.mapper.VerificationMapper;
import id.go.kemenkoinfra.ipfo.sifpi.auth.model.User;
import id.go.kemenkoinfra.ipfo.sifpi.auth.repository.UserRepository;
import id.go.kemenkoinfra.ipfo.sifpi.auth.service.AdminVerificationService;
import id.go.kemenkoinfra.ipfo.sifpi.common.exception.NotFoundException;
import id.go.kemenkoinfra.ipfo.sifpi.common.services.EmailServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminVerificationServiceImpl implements AdminVerificationService {

    private final UserRepository userRepository;
    private final VerificationMapper verificationMapper;
    private final EmailServiceImpl emailService;

    @Override
    @Transactional
    public VerificationResponseDTO verifyProjectOwner(VerifyOwnerRequest request, String adminEmail) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new NotFoundException("Pengguna dengan email " + request.getEmail() + " tidak ditemukan."));

        if (user.isVerified()) {
            throw new IllegalArgumentException("Pengguna ini sudah terverifikasi sebelumnya.");
        }

        user.setVerified(true);
        user.setVerifiedBy(adminEmail);
        user.setVerifiedAt(LocalDateTime.now());

        User verifiedUser = userRepository.save(user);
        log.info("Project Owner {} verified by admin {}", request.getEmail(), adminEmail);

        // Send notification email
        try {
            sendVerificationNotificationEmail(verifiedUser);
        } catch (Exception e) {
            log.error("Failed to send verification notification email to {}", request.getEmail(), e);
        }

        return verificationMapper.toVerificationDTO(verifiedUser);
    }

    private void sendVerificationNotificationEmail(User user) {
        String htmlContent = """
                <html>
                <body style="font-family: Arial, sans-serif;">
                    <h2>Akun Anda Telah Diverifikasi</h2>
                    <p>Halo %s,</p>
                    <p>Kami dengan senang hati memberitahu bahwa akun Anda telah diverifikasi oleh administrator IPFO SIFPI.</p>
                    <p><strong>Anda sekarang dapat mengajukan proyek ke platform.</strong></p>
                    <p>Terima kasih telah bergabung dengan kami!</p>
                    <br/>
                    <p>Salam,<br/>Tim IPFO SIFPI</p>
                </body>
                </html>
                """.formatted(user.getName());

        emailService.sendEmail(
                user.getEmail(),
                "Akun Anda Telah Diverifikasi - IPFO SIFPI",
                htmlContent
        );
    }
}
