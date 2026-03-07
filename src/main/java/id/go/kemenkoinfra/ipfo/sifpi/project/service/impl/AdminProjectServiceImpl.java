package id.go.kemenkoinfra.ipfo.sifpi.project.service.impl;

import id.go.kemenkoinfra.ipfo.sifpi.auth.model.User;
import id.go.kemenkoinfra.ipfo.sifpi.auth.repository.UserRepository;
import id.go.kemenkoinfra.ipfo.sifpi.common.enums.ProjectStatus;
import id.go.kemenkoinfra.ipfo.sifpi.common.enums.Sector;
import id.go.kemenkoinfra.ipfo.sifpi.project.dto.AdminProjectListItemDTO;
import id.go.kemenkoinfra.ipfo.sifpi.project.model.Project;
import id.go.kemenkoinfra.ipfo.sifpi.project.repository.ProjectRepository;
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
    private final UserRepository userRepository;

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
}
