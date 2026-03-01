package id.go.kemenkoinfra.ipfo.sifpi.usermanagement.service;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import id.go.kemenkoinfra.ipfo.sifpi.auth.model.Role;
import id.go.kemenkoinfra.ipfo.sifpi.auth.model.User;
import id.go.kemenkoinfra.ipfo.sifpi.auth.repository.RoleRepository;
import id.go.kemenkoinfra.ipfo.sifpi.auth.repository.UserRepository;
import id.go.kemenkoinfra.ipfo.sifpi.common.exception.ConflictException;
import id.go.kemenkoinfra.ipfo.sifpi.common.exception.NotFoundException;
import id.go.kemenkoinfra.ipfo.sifpi.common.services.EmailService;
import id.go.kemenkoinfra.ipfo.sifpi.usermanagement.dto.CreateExecutiveRequest;
import id.go.kemenkoinfra.ipfo.sifpi.usermanagement.dto.ExecutiveDTO;
import id.go.kemenkoinfra.ipfo.sifpi.usermanagement.mapper.ExecutiveMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExecutiveService {

    private static final String EXECUTIVE_ROLE_NAME = "EXECUTIVE";
    private static final int TOKEN_EXPIRY_HOURS = 24; // Token valid for 24 hours

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final ExecutiveMapper executiveMapper;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    @Transactional
    public ExecutiveDTO createExecutive(CreateExecutiveRequest request) {
        // Check if email already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ConflictException("User", "email", request.getEmail());
        }

        // Get executive role
        Role executiveRole = roleRepository.findByName(EXECUTIVE_ROLE_NAME)
                .orElseThrow(() -> new NotFoundException("Role", EXECUTIVE_ROLE_NAME));

        // Generate password setup token
        String setupToken = generateSetupToken();

        // Map request to entity
        User user = executiveMapper.toEntity(request);

        // Set fields - password is temporary, will be set by user
        user.setPassword(passwordEncoder.encode(UUID.randomUUID().toString())); // Temporary unusable password
        user.setRole(executiveRole);
        user.setVerified(false); // Will be verified after setting password
        user.setEmailVerified(false);
        user.setActive(false); // Will be activated after setting password
        user.setPasswordSetupToken(setupToken);
        user.setPasswordSetupTokenExpiry(LocalDateTime.now().plusHours(TOKEN_EXPIRY_HOURS));

        // Save user
        User savedUser = userRepository.save(user);
        log.info("Executive account created for email: {}, awaiting password setup", savedUser.getEmail());

        // Send invitation email with setup link
        emailService.sendExecutiveInvitation(
            savedUser.getEmail(),
            savedUser.getName(),
            setupToken
        );

        return executiveMapper.toDTO(savedUser);
    }

    private String generateSetupToken() {
        return UUID.randomUUID().toString().replace("-", "");
    }
}
