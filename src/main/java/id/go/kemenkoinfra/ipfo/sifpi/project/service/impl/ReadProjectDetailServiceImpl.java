package id.go.kemenkoinfra.ipfo.sifpi.project.service.impl;

import id.go.kemenkoinfra.ipfo.sifpi.auth.model.User;
import id.go.kemenkoinfra.ipfo.sifpi.auth.repository.UserRepository;
import id.go.kemenkoinfra.ipfo.sifpi.common.exception.ForbiddenException;
import id.go.kemenkoinfra.ipfo.sifpi.common.exception.NotFoundException;
import id.go.kemenkoinfra.ipfo.sifpi.common.services.StorageService;
import id.go.kemenkoinfra.ipfo.sifpi.project.dto.ProjectResponseDTO;
import id.go.kemenkoinfra.ipfo.sifpi.project.dto.ProjectStatusHistoryDTO;
import id.go.kemenkoinfra.ipfo.sifpi.project.mapper.ProjectMapper;
import id.go.kemenkoinfra.ipfo.sifpi.project.model.Project;
import id.go.kemenkoinfra.ipfo.sifpi.project.repository.ProjectRepository;
import id.go.kemenkoinfra.ipfo.sifpi.project.service.ReadProjectDetailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReadProjectDetailServiceImpl implements ReadProjectDetailService {

    private final ProjectRepository projectRepository;
    private final ProjectMapper projectMapper;
    private final StorageService storageService;
    private final UserRepository userRepository;

    /**
     * PM-4: Returns the full detail of a project owned by the authenticated project owner.
     * Throws 404 if the project does not exist, 403 if the caller is not the owner.
     */
    @Override
    @Transactional(readOnly = true)
    public ProjectResponseDTO getProjectDetail(Long id) {
        UUID currentUserId = resolveCurrentUserId();

        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Proyek", String.valueOf(id)));

        enforceOwnership(project, currentUserId);

        log.info("Project detail fetched: projectId={}, ownerId={}", id, currentUserId);

        ProjectResponseDTO dto = projectMapper.toDTO(project, storageService);

        // PM-4: rejectionReason will be populated once the status-history feature
        // (owned by a separate team member) is complete. Return null for now.
        dto.setRejectionReason(null);

        return dto;
    }

    /**
     * PM-4: Returns the status-transition history of a project owned by the authenticated PO.
     * Throws 404 if the project does not exist, 403 if the caller is not the owner.
     *
     * TODO: implementation delegated to another team member — returns null until complete.
     */
    @Override
    @Transactional(readOnly = true)
    public List<ProjectStatusHistoryDTO> getProjectHistory(Long id) {
        UUID currentUserId = resolveCurrentUserId();

        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Proyek", String.valueOf(id)));

        enforceOwnership(project, currentUserId);

        log.info("Project history fetched (stub): projectId={}, ownerId={}", id, currentUserId);

        // TODO: replace with real history query once ProjectStatusHistory entity is ready
        return null;
    }

    // -------------------------------------------------------------------------
    // Private helpers
    // -------------------------------------------------------------------------

    /**
     * Verifies that the given project belongs to {@code currentUserId}.
     * Throws {@link ForbiddenException} (→ HTTP 403) if not.
     */
    private void enforceOwnership(Project project, UUID currentUserId) {
        if (!currentUserId.equals(project.getOwnerId())) {
            log.warn("Access denied: userId={} attempted to access projectId={} owned by {}",
                    currentUserId, project.getId(), project.getOwnerId());
            throw new ForbiddenException("Anda tidak memiliki akses ke proyek ini.");
        }
    }

    /**
     * Resolves the current authenticated user's UUID from the SecurityContext.
     * Throws {@link AccessDeniedException} if there is no valid authentication.
     */
    private UUID resolveCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AccessDeniedException("Akses ditolak.");
        }

        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AccessDeniedException("Akses ditolak."));

        return user.getId();
    }
}
