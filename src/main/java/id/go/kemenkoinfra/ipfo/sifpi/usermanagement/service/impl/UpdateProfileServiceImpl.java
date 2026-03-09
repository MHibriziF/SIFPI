package id.go.kemenkoinfra.ipfo.sifpi.usermanagement.service.impl;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import id.go.kemenkoinfra.ipfo.sifpi.auth.model.InvestorProfile;
import id.go.kemenkoinfra.ipfo.sifpi.auth.model.User;
import id.go.kemenkoinfra.ipfo.sifpi.auth.repository.InvestorProfileRepository;
import id.go.kemenkoinfra.ipfo.sifpi.auth.repository.UserRepository;
import id.go.kemenkoinfra.ipfo.sifpi.auth.repository.UserTokenRepository;
import id.go.kemenkoinfra.ipfo.sifpi.common.exception.ConflictException;
import id.go.kemenkoinfra.ipfo.sifpi.common.exception.NotFoundException;
import id.go.kemenkoinfra.ipfo.sifpi.usermanagement.dto.UpdateProfileRequest;
import id.go.kemenkoinfra.ipfo.sifpi.usermanagement.dto.UserDetailDTO;
import id.go.kemenkoinfra.ipfo.sifpi.usermanagement.mapper.UserDetailMapper;
import id.go.kemenkoinfra.ipfo.sifpi.usermanagement.service.UpdateProfileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class UpdateProfileServiceImpl implements UpdateProfileService {

    private static final String ROLE_PROJECT_OWNER = "PROJECT_OWNER";
    private static final String ROLE_INVESTOR      = "INVESTOR";

    private final UserRepository userRepository;
    private final UserTokenRepository userTokenRepository;
    private final InvestorProfileRepository investorProfileRepository;
    private final UserDetailMapper userDetailMapper;

    @Override
    @Transactional
    public UserDetailDTO updateProfile(String currentEmail, UpdateProfileRequest request) {
        User user = userRepository.findByEmail(currentEmail)
                .orElseThrow(() -> new NotFoundException("User", currentEmail));

        validateEmailUniqueness(currentEmail, request.getEmail());

        applyBaseFields(user, request);

        String roleName = user.getRole() != null ? user.getRole().getName() : null;
        if (ROLE_PROJECT_OWNER.equals(roleName)) {
            applyProjectOwnerFields(user, request);
        } else if (ROLE_INVESTOR.equals(roleName)) {
            applyInvestorFields(user, request);
        }

        User saved = userRepository.save(user);

        log.info("Profile updated for user {} [role={}]", saved.getEmail(), roleName);

        return buildResponse(saved);
    }

    // ── Private helpers ──────────────────────────────────────────────────────

    private void validateEmailUniqueness(String currentEmail, String newEmail) {
        if (newEmail == null || newEmail.equalsIgnoreCase(currentEmail)) {
            return;
        }
        if (userRepository.existsByEmail(newEmail)) {
            throw new ConflictException("User", "email", newEmail);
        }
    }

    private void applyBaseFields(User user, UpdateProfileRequest request) {
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhoneNumber());
    }

    private void applyProjectOwnerFields(User user, UpdateProfileRequest request) {
        if (request.getInstitutionName() != null) {
            user.setOrganization(request.getInstitutionName());
        }
        if (request.getPosition() != null) {
            user.setJabatan(request.getPosition());
        }
    }

    private void applyInvestorFields(User user, UpdateProfileRequest request) {
        if (request.getCompanyName() != null) {
            user.setOrganization(request.getCompanyName());
        }

        InvestorProfile profile = investorProfileRepository.findByUserId(user.getId())
                .orElseGet(() -> {
                    InvestorProfile newProfile = new InvestorProfile();
                    newProfile.setUser(user);
                    return newProfile;
                });

        if (request.getInvestmentScale() != null) {
            profile.setBudgetRange(request.getInvestmentScale());
        }
        if (request.getInvestmentInterestSectors() != null) {
            profile.setSectorInterest(joinSectors(request.getInvestmentInterestSectors()));
        }

        investorProfileRepository.save(profile);
    }

    private String joinSectors(List<String> sectors) {
        if (sectors == null || sectors.isEmpty()) {
            return "";
        }
        return sectors.stream()
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.joining(","));
    }

    private UserDetailDTO buildResponse(User user) {
        UserDetailDTO dto = userDetailMapper.toUserDetailDTO(user);

        // Enrich last login
        userTokenRepository.findTopByUserIdOrderByCreatedAtDesc(user.getId())
                .ifPresent(token -> dto.setLastLogin(token.getCreatedAt()));

        // Enrich investor-specific fields
        String roleName = user.getRole() != null ? user.getRole().getName() : null;
        if (ROLE_INVESTOR.equals(roleName)) {
            investorProfileRepository.findByUserId(user.getId()).ifPresent(profile -> {
                dto.setSectorInterest(parseSectorInterest(profile.getSectorInterest()));
                dto.setBudgetRange(profile.getBudgetRange());
                dto.setCompanyInfo(UserDetailDTO.CompanyInfoDTO.builder()
                        .name(user.getOrganization())
                        .sector(parseSectorInterest(profile.getSectorInterest()).stream().findFirst().orElse(null))
                        .build());
            });
        }

        return dto;
    }

    private List<String> parseSectorInterest(String raw) {
        if (raw == null || raw.isBlank()) {
            return Collections.emptyList();
        }
        return Arrays.stream(raw.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toList();
    }
}
