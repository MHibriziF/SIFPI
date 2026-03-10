package id.go.kemenkoinfra.ipfo.sifpi.usermanagement.service.impl;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import id.go.kemenkoinfra.ipfo.sifpi.auth.model.InvestorProfile;
import id.go.kemenkoinfra.ipfo.sifpi.auth.model.User;
import id.go.kemenkoinfra.ipfo.sifpi.auth.repository.InvestorProfileRepository;
import id.go.kemenkoinfra.ipfo.sifpi.auth.repository.ProjectOwnerProfileRepository;
import id.go.kemenkoinfra.ipfo.sifpi.auth.repository.UserRepository;
import id.go.kemenkoinfra.ipfo.sifpi.auth.repository.UserTokenRepository;
import id.go.kemenkoinfra.ipfo.sifpi.common.exception.NotFoundException;
import id.go.kemenkoinfra.ipfo.sifpi.project.repository.ProjectRepository;
import id.go.kemenkoinfra.ipfo.sifpi.usermanagement.dto.UserDetailDTO;
import id.go.kemenkoinfra.ipfo.sifpi.usermanagement.mapper.UserDetailMapper;
import id.go.kemenkoinfra.ipfo.sifpi.usermanagement.service.AdminUserDetailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminUserDetailServiceImpl implements AdminUserDetailService {

    private static final String ROLE_PROJECT_OWNER = "PROJECT_OWNER";
    private static final String ROLE_INVESTOR      = "INVESTOR";

    private final UserRepository userRepository;
    private final UserTokenRepository userTokenRepository;
    private final InvestorProfileRepository investorProfileRepository;
    private final ProjectOwnerProfileRepository projectOwnerProfileRepository;
    private final ProjectRepository projectRepository;
    private final UserDetailMapper userDetailMapper;

    @Override
    @Transactional(readOnly = true)
    public UserDetailDTO getUserDetailByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("User", email));

        UserDetailDTO dto = userDetailMapper.toUserDetailDTO(user);

        enrichAuditTrail(dto, user);
        enrichRoleSpecificFields(dto, user);

        log.debug("Admin retrieved detail for user {} [role={}]", email, dto.getRole());
        return dto;
    }

    // ── Private helpers ──────────────────────────────────────────────────────

    private void enrichAuditTrail(UserDetailDTO dto, User user) {
        userTokenRepository.findTopByUserIdOrderByCreatedAtDesc(user.getId())
                .ifPresent(token -> dto.setLastLogin(token.getCreatedAt()));
        // phoneVerified remains null — field not yet tracked in the User model
    }

    private void enrichRoleSpecificFields(UserDetailDTO dto, User user) {
        String roleName = user.getRole() != null ? user.getRole().getName() : null;

        if (ROLE_PROJECT_OWNER.equals(roleName)) {
            enrichProjectOwnerFields(dto, user);
        } else if (ROLE_INVESTOR.equals(roleName)) {
            enrichInvestorFields(dto, user);
        }
        // EXECUTIVE and other roles: base user fields (email, name) are sufficient
    }

    private void enrichProjectOwnerFields(UserDetailDTO dto, User user) {
        // Set admin verification status from ProjectOwnerProfile
        projectOwnerProfileRepository.findByUserId(user.getId()).ifPresentOrElse(
            profile -> dto.setOwnerVerified(profile.isVerified()),
            () -> dto.setOwnerVerified(false)
        );

        long projectCount = projectRepository.countByOwnerId(user.getId());
        dto.setJumlahProyek((int) projectCount);
        dto.setInquiryMasuk(0); // placeholder — Inquiry feature not yet implemented
    }

    private void enrichInvestorFields(UserDetailDTO dto, User user) {
        investorProfileRepository.findByUserId(user.getId()).ifPresent(profile -> {
            dto.setCompanyInfo(buildCompanyInfo(user, profile));
            dto.setSectorInterest(parseSectorInterest(profile.getSectorInterest()));
            dto.setBudgetRange(profile.getBudgetRange());
            // projectInterest remains null — no ProjectInterest model yet
        });
    }

    private UserDetailDTO.CompanyInfoDTO buildCompanyInfo(User user, InvestorProfile profile) {
        List<String> sectors = parseSectorInterest(profile.getSectorInterest());
        String primarySector = sectors.isEmpty() ? null : sectors.get(0);

        return UserDetailDTO.CompanyInfoDTO.builder()
                .name(user.getOrganization())
                .sector(primarySector)
                .industryType(null) // not yet tracked in the model
                .build();
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
