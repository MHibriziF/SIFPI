package id.go.kemenkoinfra.ipfo.sifpi.auth.service.impl;

import id.go.kemenkoinfra.ipfo.sifpi.auth.dto.InvestorDTO;
import id.go.kemenkoinfra.ipfo.sifpi.auth.dto.request.CreateInvestorRequest;
import id.go.kemenkoinfra.ipfo.sifpi.auth.event.InvestorRegisteredEvent;
import id.go.kemenkoinfra.ipfo.sifpi.auth.mapper.RegistrationMapper;
import id.go.kemenkoinfra.ipfo.sifpi.auth.model.InvestorProfile;
import id.go.kemenkoinfra.ipfo.sifpi.auth.model.Role;
import id.go.kemenkoinfra.ipfo.sifpi.auth.model.User;
import id.go.kemenkoinfra.ipfo.sifpi.auth.repository.RoleRepository;
import id.go.kemenkoinfra.ipfo.sifpi.auth.repository.UserRepository;
import id.go.kemenkoinfra.ipfo.sifpi.auth.service.InvestorRegistrationService;
import id.go.kemenkoinfra.ipfo.sifpi.common.enums.BudgetRange;
import id.go.kemenkoinfra.ipfo.sifpi.common.enums.Sector;
import id.go.kemenkoinfra.ipfo.sifpi.common.exception.ConflictException;
import id.go.kemenkoinfra.ipfo.sifpi.common.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class InvestorRegistrationServiceImpl implements InvestorRegistrationService {

    private static final String ROLE_INVESTOR = "INVESTOR";
    private static final int EMAIL_VERIFICATION_TOKEN_EXPIRY_MINUTES = 25;

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final RegistrationMapper registrationMapper;
    private final PasswordEncoder passwordEncoder;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public InvestorDTO registerInvestor(CreateInvestorRequest request) {
        log.info("Registering new investor with email: {}", request.getEmail());
        // --- validation: ensure budget & sector values are allowed by system metadata ---
        if (!BudgetRange.isValidValue(request.getBudgetInvestasi())) {
            throw new IllegalArgumentException("Budget investasi tidak valid");
        }
        // Validasi email duplicate
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ConflictException("Email sudah terdaftar");
        }

        // Validasi password match
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new IllegalArgumentException("Password dan konfirmasi password tidak cocok");
        }

        // Validasi sector interest minimal 3
        if (request.getSectorInterest() == null || request.getSectorInterest().size() < 3) {
            throw new IllegalArgumentException("Minimal 3 sektor prioritas harus dipilih");
        }

        // validate each sector against allowed options
        java.util.List<String> invalidSectors = request.getSectorInterest().stream()
                .filter(s -> !Sector.isValidValue(s))
                .distinct()
                .toList();

        if (!invalidSectors.isEmpty()) {
            throw new IllegalArgumentException("Sektor tidak valid: " + String.join(", ", invalidSectors));
        }

        // Validasi agreePrivacy must be true
        if (request.getAgreePrivacy() == null || !request.getAgreePrivacy()) {
            throw new IllegalArgumentException("Anda harus menyetujui syarat dan kebijakan privasi");
        }

        // Get role
        Role investorRole = roleRepository.findByName(ROLE_INVESTOR)
                .orElseThrow(() -> new NotFoundException("Role", ROLE_INVESTOR));

        // Map request to entity
        User user = registrationMapper.toEntityInvestor(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(investorRole);

        // Generate email verification token
        String verificationToken = UUID.randomUUID().toString().replace("-", "");
        user.setEmailVerificationToken(verificationToken);
        user.setEmailVerificationTokenExpiry(LocalDateTime.now().plusMinutes(EMAIL_VERIFICATION_TOKEN_EXPIRY_MINUTES));

        // Create investor profile and set bidirectional relationship
        InvestorProfile investorProfile = registrationMapper.toInvestorProfile(request);
        investorProfile.setUser(user);
        user.setInvestorProfile(investorProfile);

        // Save user to database (emailVerified = false initially, cascade will save investor profile automatically)
        User savedUser = userRepository.saveAndFlush(user);
        log.info("Investor user created with id: {}, profile cascade-saved, awaiting email verification", savedUser.getId());

        // Publish event for email sending (after transaction commits)
        eventPublisher.publishEvent(new InvestorRegisteredEvent(savedUser, request));

        return registrationMapper.toInvestorDTO(savedUser);
    }
}
