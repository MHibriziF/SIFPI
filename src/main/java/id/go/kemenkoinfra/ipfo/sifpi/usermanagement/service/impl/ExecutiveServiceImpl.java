package id.go.kemenkoinfra.ipfo.sifpi.usermanagement.service.impl;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import id.go.kemenkoinfra.ipfo.sifpi.auth.model.Role;
import id.go.kemenkoinfra.ipfo.sifpi.auth.model.User;
import id.go.kemenkoinfra.ipfo.sifpi.auth.repository.RoleRepository;
import id.go.kemenkoinfra.ipfo.sifpi.auth.repository.UserRepository;
import id.go.kemenkoinfra.ipfo.sifpi.common.config.EmailProperties;
import id.go.kemenkoinfra.ipfo.sifpi.common.exception.ConflictException;
import id.go.kemenkoinfra.ipfo.sifpi.common.exception.NotFoundException;
import id.go.kemenkoinfra.ipfo.sifpi.common.services.EmailService;
import id.go.kemenkoinfra.ipfo.sifpi.common.utils.EmailTemplateUtil;
import id.go.kemenkoinfra.ipfo.sifpi.usermanagement.dto.CreateExecutiveRequest;
import id.go.kemenkoinfra.ipfo.sifpi.usermanagement.dto.ExecutiveDTO;
import id.go.kemenkoinfra.ipfo.sifpi.usermanagement.mapper.ExecutiveMapper;
import id.go.kemenkoinfra.ipfo.sifpi.usermanagement.service.ExecutiveService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExecutiveServiceImpl implements ExecutiveService {

    private static final String EXECUTIVE_ROLE_NAME = "EXECUTIVE";
    private static final int TOKEN_EXPIRY_HOURS = 24;

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final ExecutiveMapper executiveMapper;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final EmailTemplateUtil emailTemplateUtil;
    private final EmailProperties emailProperties;

    @Override
    @Transactional
    public ExecutiveDTO createExecutive(CreateExecutiveRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ConflictException("User", "email", request.getEmail());
        }

        Role executiveRole = roleRepository.findByName(EXECUTIVE_ROLE_NAME)
                .orElseThrow(() -> new NotFoundException("Role", EXECUTIVE_ROLE_NAME));

        String setupToken = UUID.randomUUID().toString().replace("-", "");

        User user = executiveMapper.toEntity(request);
        user.setPassword(passwordEncoder.encode(UUID.randomUUID().toString()));
        user.setRole(executiveRole);
        user.setVerified(false);
        user.setEmailVerified(false);
        user.setActive(false);
        user.setPasswordSetupToken(setupToken);
        user.setPasswordSetupTokenExpiry(LocalDateTime.now().plusHours(TOKEN_EXPIRY_HOURS));

        User savedUser = userRepository.save(user);
        log.info("Executive account created for email: {}, awaiting password setup", savedUser.getEmail());

        sendInvitationEmail(savedUser.getName(), savedUser.getEmail(), setupToken);

        return executiveMapper.toDTO(savedUser);
    }

    private void sendInvitationEmail(String name, String email, String setupToken) {
        String setupUrl = emailProperties.getLoginUrl().replace("/login", "/set-password?token=" + setupToken);

        // Log token for development/testing
        log.warn("=".repeat(80));
        log.warn("EMAIL INVITATION - TOKEN FOR TESTING");
        log.warn("To: {}", email);
        log.warn("Setup URL: {}", setupUrl);
        log.warn("=".repeat(80));

        String html = emailTemplateUtil.load("executive-invitation", Map.of(
                "name", name,
                "email", email,
                "setupUrl", setupUrl
        ));

        emailService.sendEmail(email, "Undangan Akun Executive - SIFPI", html);
    }
}
