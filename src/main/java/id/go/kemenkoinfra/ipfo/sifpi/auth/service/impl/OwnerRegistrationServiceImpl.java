package id.go.kemenkoinfra.ipfo.sifpi.auth.service.impl;

import id.go.kemenkoinfra.ipfo.sifpi.auth.dto.OwnerDTO;
import id.go.kemenkoinfra.ipfo.sifpi.auth.dto.request.CreateOwnerRequest;
import id.go.kemenkoinfra.ipfo.sifpi.auth.event.OwnerRegisteredEvent;
import id.go.kemenkoinfra.ipfo.sifpi.auth.mapper.RegistrationMapper;
import id.go.kemenkoinfra.ipfo.sifpi.auth.model.ProjectOwnerProfile;
import id.go.kemenkoinfra.ipfo.sifpi.auth.model.Role;
import id.go.kemenkoinfra.ipfo.sifpi.auth.model.User;
import id.go.kemenkoinfra.ipfo.sifpi.auth.repository.ProjectOwnerRepository;
import id.go.kemenkoinfra.ipfo.sifpi.auth.repository.RoleRepository;
import id.go.kemenkoinfra.ipfo.sifpi.auth.repository.UserRepository;
import id.go.kemenkoinfra.ipfo.sifpi.auth.service.OwnerRegistrationService;
import id.go.kemenkoinfra.ipfo.sifpi.auth.service.UserTokenService;
import id.go.kemenkoinfra.ipfo.sifpi.common.enums.TokenType;
import id.go.kemenkoinfra.ipfo.sifpi.common.exception.ConflictException;
import id.go.kemenkoinfra.ipfo.sifpi.common.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class OwnerRegistrationServiceImpl implements OwnerRegistrationService {

    private static final String ROLE_PROJECT_OWNER = "PROJECT_OWNER";
    private static final int EMAIL_VERIFICATION_TOKEN_EXPIRY_MINUTES = 25;

    private final UserRepository userRepository;
    private final ProjectOwnerRepository projectOwnerRepository;
    private final UserTokenService userTokenService;
    private final RoleRepository roleRepository;
    private final RegistrationMapper registrationMapper;
    private final PasswordEncoder passwordEncoder;
    private final ApplicationEventPublisher eventPublisher;

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

        // Save user to database (emailVerified = false initially)
        User savedUser = userRepository.saveAndFlush(user);
        log.info("Project owner registered successfully with id: {}, awaiting email verification", savedUser.getId());

        // Create ProjectOwnerProfile for this user
        ProjectOwnerProfile ownerProfile = new ProjectOwnerProfile();
        ownerProfile.setUser(savedUser);
        ownerProfile.setVerified(false);  // Admin verification pending
        projectOwnerRepository.saveAndFlush(ownerProfile);
        log.info("ProjectOwnerProfile created for user: {}", savedUser.getId());

        // Generate and save email verification token
        String verificationToken = userTokenService.createToken(
                savedUser, TokenType.EMAIL_VERIFICATION,
                LocalDateTime.now().plusMinutes(EMAIL_VERIFICATION_TOKEN_EXPIRY_MINUTES));

        // Publish event untuk email (dikirim setelah DB commit berhasil)
        eventPublisher.publishEvent(new OwnerRegisteredEvent(savedUser, request, verificationToken));

        return registrationMapper.toOwnerDTO(savedUser);
    }
}
