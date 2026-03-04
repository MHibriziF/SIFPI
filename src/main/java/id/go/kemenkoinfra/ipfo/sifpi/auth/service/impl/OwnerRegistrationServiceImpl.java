package id.go.kemenkoinfra.ipfo.sifpi.auth.service.impl;

import id.go.kemenkoinfra.ipfo.sifpi.auth.dto.OwnerDTO;
import id.go.kemenkoinfra.ipfo.sifpi.auth.dto.request.CreateOwnerRequest;
import id.go.kemenkoinfra.ipfo.sifpi.auth.mapper.RegistrationMapper;
import id.go.kemenkoinfra.ipfo.sifpi.auth.model.Role;
import id.go.kemenkoinfra.ipfo.sifpi.auth.model.User;
import id.go.kemenkoinfra.ipfo.sifpi.auth.repository.RoleRepository;
import id.go.kemenkoinfra.ipfo.sifpi.auth.repository.UserRepository;
import id.go.kemenkoinfra.ipfo.sifpi.auth.service.OwnerRegistrationService;
import id.go.kemenkoinfra.ipfo.sifpi.common.config.EmailProperties;
import id.go.kemenkoinfra.ipfo.sifpi.common.exception.ConflictException;
import id.go.kemenkoinfra.ipfo.sifpi.common.exception.NotFoundException;
import id.go.kemenkoinfra.ipfo.sifpi.common.services.EmailService;
import id.go.kemenkoinfra.ipfo.sifpi.common.utils.EmailTemplateUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class OwnerRegistrationServiceImpl implements OwnerRegistrationService {

    private static final String ROLE_PROJECT_OWNER = "PROJECT_OWNER";

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final RegistrationMapper registrationMapper;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final EmailTemplateUtil emailTemplateUtil;
    private final EmailProperties emailProperties;

    @Transactional
    public OwnerDTO registerOwner(CreateOwnerRequest request) {
        log.info("Registering new project owner with email: {}", request.getEmail());

        // Validasi email duplicate
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ConflictException("Email sudah terdaftar");
        }

        // Validasi password match
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new IllegalArgumentException("Password dan konfirmasi password tidak cocok");
        }

        // Get role
        Role ownerRole = roleRepository.findByName(ROLE_PROJECT_OWNER)
                .orElseThrow(() -> new NotFoundException("Role", ROLE_PROJECT_OWNER));

        // Map request to entity
        User user = registrationMapper.toEntityOwner(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(ownerRole);

        // Save user (JPA @PrePersist will set createdAt before persist)
        User savedUser = userRepository.save(user);
        log.info("Project owner registered successfully with id: {}", savedUser.getId());

        // Send welcome email
        sendRegistrationEmail(savedUser);

        return registrationMapper.toOwnerDTO(savedUser);
    }

    private void sendRegistrationEmail(User user) {
        try {
            String html = emailTemplateUtil.load("owner-registration", Map.of(
                    "name", user.getName(),
                    "email", user.getEmail(),
                    "loginUrl", emailProperties.getLoginUrl()
            ));

            emailService.sendEmail(
                    user.getEmail(),
                    "Selamat Datang - Registrasi Project Owner SIFPI",
                    html
            );
            log.info("Registration email sent successfully to: {}", user.getEmail());
        } catch (Exception e) {
            log.warn("Failed to send registration email to {}: {}", user.getEmail(), e.getMessage());
            // Don't throw - registration is complete, email delivery is best-effort
        }
    }
}
