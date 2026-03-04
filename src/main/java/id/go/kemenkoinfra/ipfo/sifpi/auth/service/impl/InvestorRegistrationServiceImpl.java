package id.go.kemenkoinfra.ipfo.sifpi.auth.service.impl;

import id.go.kemenkoinfra.ipfo.sifpi.auth.dto.InvestorDTO;
import id.go.kemenkoinfra.ipfo.sifpi.auth.dto.request.CreateInvestorRequest;
import id.go.kemenkoinfra.ipfo.sifpi.auth.mapper.RegistrationMapper;
import id.go.kemenkoinfra.ipfo.sifpi.auth.model.BudgetRange;
import id.go.kemenkoinfra.ipfo.sifpi.auth.model.InvestorProfile;
import id.go.kemenkoinfra.ipfo.sifpi.auth.model.Role;
import id.go.kemenkoinfra.ipfo.sifpi.auth.model.Sector;
import id.go.kemenkoinfra.ipfo.sifpi.auth.model.User;
import id.go.kemenkoinfra.ipfo.sifpi.auth.repository.InvestorProfileRepository;
import id.go.kemenkoinfra.ipfo.sifpi.auth.repository.RoleRepository;
import id.go.kemenkoinfra.ipfo.sifpi.auth.repository.UserRepository;
import id.go.kemenkoinfra.ipfo.sifpi.auth.service.InvestorRegistrationService;
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
public class InvestorRegistrationServiceImpl implements InvestorRegistrationService {

    private static final String ROLE_INVESTOR = "INVESTOR";

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final InvestorProfileRepository investorProfileRepository;
    private final RegistrationMapper registrationMapper;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final EmailTemplateUtil emailTemplateUtil;
    private final EmailProperties emailProperties;

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

        // Save user first (JPA @PrePersist will set createdAt before persist)
        User savedUser = userRepository.save(user);
        log.info("Investor user created with id: {}", savedUser.getId());

        // Create investor profile
        InvestorProfile investorProfile = registrationMapper.toInvestorProfile(request);
        investorProfile.setUser(savedUser);
        InvestorProfile savedProfile = investorProfileRepository.save(investorProfile);
        log.info("Investor profile created for user id: {}", savedUser.getId());

        // Set bidirectional relationship
        savedUser.setInvestorProfile(savedProfile);
        userRepository.save(savedUser);

        // Force load investor profile for DTO mapping
        User userWithProfile = userRepository.findById(savedUser.getId())
                .orElseThrow(() -> new NotFoundException("User", savedUser.getId().toString()));

        // Send welcome email
        sendRegistrationEmail(userWithProfile, request);

        return registrationMapper.toInvestorDTO(userWithProfile);
    }

    private void sendRegistrationEmail(User user, CreateInvestorRequest request) {
        try {
            String sectors = String.join(", ", request.getSectorInterest());
            
            String html = emailTemplateUtil.load("investor-registration", Map.of(
                    "name", user.getName(),
                    "email", user.getEmail(),
                    "loginUrl", emailProperties.getLoginUrl(),
                    "sectors", sectors,
                    "budget", request.getBudgetInvestasi() != null ? request.getBudgetInvestasi() : "-"
            ));

            emailService.sendEmail(
                    user.getEmail(),
                    "Selamat Datang - Registrasi Investor SIFPI",
                    html
            );
            log.info("Registration email sent successfully to: {}", user.getEmail());
        } catch (Exception e) {
            log.warn("Failed to send registration email to {}: {}", user.getEmail(), e.getMessage());
            // Don't throw - registration is complete, email delivery is best-effort
        }
    }
}
