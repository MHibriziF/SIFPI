package id.go.kemenkoinfra.ipfo.sifpi.project.service.impl;

import id.go.kemenkoinfra.ipfo.sifpi.auth.model.User;
import id.go.kemenkoinfra.ipfo.sifpi.auth.repository.UserRepository;
import id.go.kemenkoinfra.ipfo.sifpi.common.enums.ProjectStatus;
import id.go.kemenkoinfra.ipfo.sifpi.common.enums.Sector;
import id.go.kemenkoinfra.ipfo.sifpi.common.exception.NotFoundException;
import id.go.kemenkoinfra.ipfo.sifpi.common.services.StorageService;
import id.go.kemenkoinfra.ipfo.sifpi.project.dto.AdminProjectDetailDTO;
import id.go.kemenkoinfra.ipfo.sifpi.project.dto.AdminProjectListItemDTO;
import id.go.kemenkoinfra.ipfo.sifpi.project.dto.ProjectResponseDTO;
import id.go.kemenkoinfra.ipfo.sifpi.project.dto.ProjectStatusHistoryDTO;
import id.go.kemenkoinfra.ipfo.sifpi.project.dto.ProjectVerificationDTO;
import id.go.kemenkoinfra.ipfo.sifpi.project.dto.ProjectTimelineResponseDTO;
import id.go.kemenkoinfra.ipfo.sifpi.project.dto.request.RejectProjectRequest;
import id.go.kemenkoinfra.ipfo.sifpi.project.dto.request.BulkPublishRequest;
import id.go.kemenkoinfra.ipfo.sifpi.project.mapper.ProjectMapper;
import id.go.kemenkoinfra.ipfo.sifpi.project.model.Project;
import id.go.kemenkoinfra.ipfo.sifpi.project.model.ProjectVerification;
import id.go.kemenkoinfra.ipfo.sifpi.project.repository.ProjectRepository;
import id.go.kemenkoinfra.ipfo.sifpi.project.repository.ProjectVerificationRepository;
import id.go.kemenkoinfra.ipfo.sifpi.project.service.AdminProjectService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * PM-9: Implementation of admin project service
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AdminProjectServiceImpl implements AdminProjectService {

    private final ProjectRepository projectRepository;
    private final ProjectVerificationRepository projectVerificationRepository;
    private final UserRepository userRepository;
    private final ProjectMapper projectMapper;
    private final StorageService storageService;

    @Override
    @Transactional(readOnly = true)
    public Page<AdminProjectListItemDTO> getAllProjects(
            ProjectStatus status,
            Sector sector,
            UUID ownerId,
            String search,
            Pageable pageable) {

        log.info("Admin fetching all projects - status: {}, sector: {}, ownerId: {}, search: '{}', pageable: {}",
                status, sector, ownerId, search, pageable);

        // Fetch projects with filters
        Page<Project> projectPage = projectRepository.findAllWithFilters(
                status,
                sector,
                ownerId,
                search,
                pageable
        );

        log.info("Found {} projects matching filters", projectPage.getTotalElements());

        // Collect all unique owner IDs
        Set<UUID> ownerIds = new HashSet<>();
        projectPage.getContent().forEach(project -> {
            if (project.getOwnerId() != null) {
                ownerIds.add(project.getOwnerId());
            }
        });

        // Fetch all owners in one query
        Map<UUID, User> ownerMap = new HashMap<>();
        if (!ownerIds.isEmpty()) {
            List<User> owners = userRepository.findAllById(ownerIds);
            owners.forEach(user -> ownerMap.put(user.getId(), user));
        }

        // Map to DTOs with owner information
        return projectPage.map(project -> mapToAdminDTO(project, ownerMap));
    }

    /**
     * Map Project entity to AdminProjectListItemDTO with owner information
     */
    private AdminProjectListItemDTO mapToAdminDTO(Project project, Map<UUID, User> ownerMap) {
        User owner = ownerMap.get(project.getOwnerId());

        return AdminProjectListItemDTO.builder()
                .id(project.getId())
                .name(project.getName())
                .sector(project.getSector())
                .status(project.getStatus())
                .ownerId(project.getOwnerId())
                .ownerName(owner != null ? owner.getName() : "Unknown")
                .ownerOrganization(owner != null ? owner.getOrganization() : project.getOwnerInstitution())
                .totalCapex(project.getTotalCapex())
                .totalOpex(project.getTotalOpex())
                .createdAt(project.getCreatedAt())
                .updatedAt(project.getEditedAt())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public AdminProjectDetailDTO getProjectDetail(Long projectId) {
        log.info("Admin fetching project detail - projectId: {}", projectId);

        // Fetch project with all relationships
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> {
                    log.warn("Project not found - projectId: {}", projectId);
                    return new NotFoundException("Proyek dengan ID " + projectId + " tidak ditemukan");
                });

        // Fetch owner information
        User owner = null;
        if (project.getOwnerId() != null) {
            owner = userRepository.findById(project.getOwnerId()).orElse(null);
        }

        // Map to detail DTO
        AdminProjectDetailDTO detailDTO = projectMapper.toAdminDetailDTO(project);
        
        // Set owner information
        if (owner != null) {
            detailDTO.setOwnerId(owner.getId());
            detailDTO.setOwnerName(owner.getName());
            detailDTO.setOwnerEmail(owner.getEmail());
            detailDTO.setOwnerPhone(owner.getPhone());
            detailDTO.setOwnerOrganization(owner.getOrganization());
        }

        // Set image URLs — use the backend proxy endpoint so files are served
        // through the authenticated backend rather than directly from storage
        // (avoids CORS / SSL issues with raw storage URLs).
        if (project.getLocationImageKey() != null && !project.getLocationImageKey().isBlank()) {
            detailDTO.setLocationImageUrl("/api/admin/projects/" + projectId + "/files/map");
        }
        if (project.getProjectStructureImageKey() != null && !project.getProjectStructureImageKey().isBlank()) {
            detailDTO.setProjectStructureImageUrl("/api/admin/projects/" + projectId + "/files/structure");
        }
        if (project.getProjectFileKey() != null && !project.getProjectFileKey().isBlank()) {
            detailDTO.setProjectFileDownloadUrl("/api/admin/projects/" + projectId + "/files/document");
        }

        // Map timelines (using manual mapping since list mapping is straightforward)
        if (project.getTimelines() != null && !project.getTimelines().isEmpty()) {
            var timelineList = project.getTimelines().stream()
                    .map(timeline -> ProjectTimelineResponseDTO.builder()
                            .id(timeline.getId())
                            .timeRange(timeline.getTimeRange())
                            .phaseDescription(timeline.getPhaseDescription())
                            .build())
                    .toList();
            detailDTO.setTimelines(timelineList);
        }

        // Map status history from project status histories
        if (project.getStatusHistories() != null && !project.getStatusHistories().isEmpty()) {
            var statusHistoryList = project.getStatusHistories().stream()
                    .map(statusHistory -> ProjectStatusHistoryDTO.builder()
                            .id(statusHistory.getId())
                            .status(statusHistory.getStatus())
                            .changedBy(statusHistory.getChangedBy())
                            .changedByName(statusHistory.getChangedByName())
                            .notes(statusHistory.getNotes())
                            .changedAt(statusHistory.getChangedAt())
                            .build())
                    .toList();
            detailDTO.setStatusHistory(statusHistoryList);
        } else {
            // Initialize empty list if no status histories
            detailDTO.setStatusHistory(new ArrayList<>());
        }

        // Map verifications from project verifications
        if (project.getVerifications() != null && !project.getVerifications().isEmpty()) {
            var verificationList = project.getVerifications().stream()
                    .map(verification -> ProjectVerificationDTO.builder()
                            .id(verification.getId())
                            .action(verification.getAction())
                            .notes(verification.getNotes())
                            .verifiedBy(verification.getVerifiedBy())
                            .verifiedByName(verification.getVerifiedByName())
                            .verifiedAt(verification.getVerifiedAt())
                            .build())
                    .toList();
            detailDTO.setVerifications(verificationList);
        } else {
            // Initialize empty list if no verifications
            detailDTO.setVerifications(new ArrayList<>());
        }

        log.info("Successfully retrieved project detail - projectId: {}, name: {}", projectId, project.getName());
        return detailDTO;
    }

    @Override
    @Transactional
    public ProjectResponseDTO approveProject(Long projectId, UUID adminId) {
        log.info("Admin approving project - projectId: {}, adminId: {}", projectId, adminId);

        // Fetch project
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> {
                    log.warn("Project not found - projectId: {}", projectId);
                    return new NotFoundException("Proyek dengan ID " + projectId + " tidak ditemukan");
                });

        // Validate status is DIAJUKAN or IN_REVIEW
        if (!project.getStatus().equals(ProjectStatus.IN_REVIEW) && !project.getStatus().equals(ProjectStatus.DIAJUKAN)) {
            log.warn("Cannot approve project - status is not DIAJUKAN or IN_REVIEW - projectId: {}, status: {}", projectId, project.getStatus());
            throw new java.lang.IllegalArgumentException("Hanya proyek dengan status 'Diajukan' atau 'In Review' yang bisa di-approve");
        }

        // Get admin name
        User admin = userRepository.findById(adminId)
                .orElseThrow(() -> new NotFoundException("Admin tidak ditemukan"));

        // Update status to VERIFIED
        project.setStatus(ProjectStatus.TERVERIFIKASI);
        Project updatedProject = projectRepository.save(project);

        // Record verification
        ProjectVerification verification = ProjectVerification.builder()
                .project(updatedProject)
                .action("VERIFIED")
                .verifiedBy(adminId)
                .verifiedByName(admin.getName())
                .notes("Proyek disetujui oleh admin")
                .build();
        projectVerificationRepository.save(verification);

        log.info("Successfully approved project - projectId: {}, newStatus: {}", projectId, updatedProject.getStatus());
        return projectMapper.toDTO(updatedProject, storageService);
    }

    @Override
    @Transactional
    public ProjectResponseDTO rejectProject(Long projectId, RejectProjectRequest request, UUID adminId) {
        log.info("Admin rejecting project - projectId: {}, adminId: {}", projectId, adminId);

        // Fetch project
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> {
                    log.warn("Project not found - projectId: {}", projectId);
                    return new NotFoundException("Proyek dengan ID " + projectId + " tidak ditemukan");
                });

        // Validate status is DIAJUKAN or IN_REVIEW
        if (!project.getStatus().equals(ProjectStatus.IN_REVIEW) && !project.getStatus().equals(ProjectStatus.DIAJUKAN)) {
            log.warn("Cannot reject project - status is not DIAJUKAN or IN_REVIEW - projectId: {}, status: {}", projectId, project.getStatus());
            throw new java.lang.IllegalArgumentException("Hanya proyek dengan status 'Diajukan' atau 'In Review' yang bisa di-reject");
        }

        // Get admin name
        User admin = userRepository.findById(adminId)
                .orElseThrow(() -> new NotFoundException("Admin tidak ditemukan"));

        // Update status to PERBAIKAN_DATA
        project.setStatus(ProjectStatus.PERBAIKAN_DATA);
        Project updatedProject = projectRepository.save(project);

        // Record verification
        ProjectVerification verification = ProjectVerification.builder()
                .project(updatedProject)
                .action("REJECTED")
                .verifiedBy(adminId)
                .verifiedByName(admin.getName())
                .notes(request.getNotes())
                .build();
        projectVerificationRepository.save(verification);

        log.info("Successfully rejected project - projectId: {}, newStatus: {}", projectId, updatedProject.getStatus());
        return projectMapper.toDTO(updatedProject, storageService);
    }

    @Override
    @Transactional
    public int bulkPublishProjects(BulkPublishRequest request, UUID adminId) {
        log.info("Admin bulk publishing projects - count: {}, adminId: {}", request.getProjectIds().size(), adminId);

        // Get admin name
        User admin = userRepository.findById(adminId)
                .orElseThrow(() -> new NotFoundException("Admin tidak ditemukan"));

        int publishedCount = 0;

        for (Long projectId : request.getProjectIds()) {
            try {
                // Fetch project
                Project project = projectRepository.findById(projectId)
                        .orElse(null);

                if (project == null) {
                    log.warn("Project not found for publishing - projectId: {}", projectId);
                    continue;
                }

                // Only publish APPROVED projects
                if (!project.getStatus().equals(ProjectStatus.TERVERIFIKASI)) {
                    log.warn("Cannot publish project - status is not APPROVED - projectId: {}, status: {}", projectId, project.getStatus());
                    continue;
                }

                // Update status to PUBLISHED
                project.setStatus(ProjectStatus.TERPUBLIKASI);
                projectRepository.save(project);

                // Record verification
                ProjectVerification verification = ProjectVerification.builder()
                        .project(project)
                        .action("PUBLISHED")
                        .verifiedBy(adminId)
                        .verifiedByName(admin.getName())
                        .notes("Proyek dipublikasikan")
                        .build();
                projectVerificationRepository.save(verification);

                publishedCount++;
                log.info("Published project - projectId: {}", projectId);
            } catch (Exception e) {
                log.error("Error publishing project - projectId: {}, error: {}", projectId, e.getMessage());
            }
        }

        log.info("Bulk publish completed - publishedCount: {}", publishedCount);
        return publishedCount;
    }

    @Override
    @Transactional
    public int bulkUnpublishProjects(BulkPublishRequest request, UUID adminId) {
        log.info("Admin bulk unpublishing projects - count: {}, adminId: {}", request.getProjectIds().size(), adminId);

        // Get admin name
        User admin = userRepository.findById(adminId)
                .orElseThrow(() -> new NotFoundException("Admin tidak ditemukan"));

        int unpublishedCount = 0;

        for (Long projectId : request.getProjectIds()) {
            try {
                // Fetch project
                Project project = projectRepository.findById(projectId)
                        .orElse(null);

                if (project == null) {
                    log.warn("Project not found for unpublishing - projectId: {}", projectId);
                    continue;
                }

                // Only unpublish PUBLISHED projects
                if (!project.getStatus().equals(ProjectStatus.TERPUBLIKASI)) {
                    log.warn("Cannot unpublish project - status is not PUBLISHED - projectId: {}, status: {}", projectId, project.getStatus());
                    continue;
                }

                // Update status back to APPROVED
                project.setStatus(ProjectStatus.TERVERIFIKASI);
                projectRepository.save(project);

                // Record verification
                ProjectVerification verification = ProjectVerification.builder()
                        .project(project)
                        .action("UNPUBLISHED")
                        .verifiedBy(adminId)
                        .verifiedByName(admin.getName())
                        .notes("Proyek di-unpublish")
                        .build();
                projectVerificationRepository.save(verification);

                unpublishedCount++;
                log.info("Unpublished project - projectId: {}", projectId);
            } catch (Exception e) {
                log.error("Error unpublishing project - projectId: {}, error: {}", projectId, e.getMessage());
            }
        }

        log.info("Bulk unpublish completed - unpublishedCount: {}", unpublishedCount);
        return unpublishedCount;
    }
}
