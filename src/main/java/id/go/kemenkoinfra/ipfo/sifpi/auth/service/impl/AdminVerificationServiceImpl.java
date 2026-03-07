package id.go.kemenkoinfra.ipfo.sifpi.auth.service.impl;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import id.go.kemenkoinfra.ipfo.sifpi.auth.dto.VerificationResponseDTO;
import id.go.kemenkoinfra.ipfo.sifpi.auth.mapper.VerificationMapper;
import id.go.kemenkoinfra.ipfo.sifpi.auth.model.ProjectOwnerProfile;
import id.go.kemenkoinfra.ipfo.sifpi.auth.model.User;
import id.go.kemenkoinfra.ipfo.sifpi.auth.repository.ProjectOwnerRepository;
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
    private final ProjectOwnerRepository projectOwnerRepository;
    private final VerificationMapper verificationMapper;
    private final EmailServiceImpl emailService;

    @Override
    @Transactional
    public VerificationResponseDTO verifyProjectOwner(String projectOwnerEmail, String adminEmail) {
        // Get user by email
        User user = userRepository.findByEmail(projectOwnerEmail)
                .orElseThrow(() -> new NotFoundException("Pengguna dengan email " + projectOwnerEmail + " tidak ditemukan."));

        // Get ProjectOwnerProfile
        ProjectOwnerProfile poProfile = projectOwnerRepository.findByUserEmail(projectOwnerEmail)
                .orElseThrow(() -> new NotFoundException("Project Owner profile tidak ditemukan untuk email " + projectOwnerEmail));

        // Check if already verified
        if (poProfile.isVerified()) {
            throw new IllegalArgumentException("Pengguna ini sudah terverifikasi sebelumnya.");
        }

        // Update verification status
        poProfile.setVerified(true);
        poProfile.setVerifiedBy(adminEmail);
        poProfile.setVerifiedAt(LocalDateTime.now());

        ProjectOwnerProfile verifiedProfile = projectOwnerRepository.save(poProfile);
        log.info("Project Owner {} verified by admin {}", projectOwnerEmail, adminEmail);

        // Send notification email
        try {
            sendVerificationNotificationEmail(user);
        } catch (Exception e) {
            log.error("Failed to send verification notification email to {}", projectOwnerEmail, e);
        }

        return verificationMapper.toVerificationDTO(user, verifiedProfile);
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
