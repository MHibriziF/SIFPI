package id.go.kemenkoinfra.ipfo.sifpi.project.service.impl;

import id.go.kemenkoinfra.ipfo.sifpi.auth.model.User;
import id.go.kemenkoinfra.ipfo.sifpi.auth.repository.UserRepository;
import id.go.kemenkoinfra.ipfo.sifpi.common.dto.PagedResponseDTO;
import id.go.kemenkoinfra.ipfo.sifpi.common.enums.ProjectStatus;
import id.go.kemenkoinfra.ipfo.sifpi.common.enums.Sector;
import id.go.kemenkoinfra.ipfo.sifpi.project.dto.AdminProjectListItemDTO;
import id.go.kemenkoinfra.ipfo.sifpi.project.model.Project;
import id.go.kemenkoinfra.ipfo.sifpi.project.repository.ProjectRepository;
import id.go.kemenkoinfra.ipfo.sifpi.project.service.AdminProjectService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

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
    public PagedResponseDTO<AdminProjectListItemDTO> getAllProjects(
            ProjectStatus status,
            Sector sector,
            UUID ownerId,
            String search,
            int page,
            int size,
            String sortBy,
            String sortDirection) {

        log.info("Admin fetching all projects - status: {}, sector: {}, ownerId: {}, search: '{}', page: {}, size: {}, sortBy: {}, sortDirection: {}",
                status, sector, ownerId, search, page, size, sortBy, sortDirection);

        // Validate and set default values
        if (page < 0) page = 0;
        if (size <= 0 || size > 100) size = 20; // Default 20 per page, max 100
        if (sortBy == null || sortBy.isBlank()) sortBy = "createdAt";
        if (sortDirection == null || sortDirection.isBlank()) sortDirection = "desc";

        // Map sortBy field for entity
        String entitySortField = mapSortField(sortBy);

        // Create sort object
        Sort.Direction direction = sortDirection.equalsIgnoreCase("asc")
                ? Sort.Direction.ASC
                : Sort.Direction.DESC;
        Sort sort = Sort.by(direction, entitySortField);

        // Create pageable
        Pageable pageable = PageRequest.of(page, size, sort);

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
        Set<UUID> ownerIds = projectPage.getContent().stream()
                .map(Project::getOwnerId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        // Fetch all owners in one query
        Map<UUID, User> ownerMap = new HashMap<>();
        if (!ownerIds.isEmpty()) {
            List<User> owners = userRepository.findAllById(ownerIds);
            ownerMap = owners.stream()
                    .collect(Collectors.toMap(User::getId, user -> user));
        }

        // Map to DTOs with owner information
        final Map<UUID, User> finalOwnerMap = ownerMap;
        List<AdminProjectListItemDTO> projectDTOs = projectPage.getContent().stream()
                .map(project -> mapToAdminDTO(project, finalOwnerMap))
                .collect(Collectors.toList());

        // Build paged response
        return PagedResponseDTO.<AdminProjectListItemDTO>builder()
                .content(projectDTOs)
                .page(projectPage.getNumber())
                .size(projectPage.getSize())
                .totalElements(projectPage.getTotalElements())
                .totalPages(projectPage.getTotalPages())
                .first(projectPage.isFirst())
                .last(projectPage.isLast())
                .build();
    }

    /**
     * Map sortBy parameter to actual entity field name
     */
    private String mapSortField(String sortBy) {
        return switch (sortBy.toLowerCase()) {
            case "createdat", "created_at" -> "createdAt";
            case "updatedat", "updated_at", "editedat", "edited_at" -> "editedAt";
            case "name", "title" -> "name";
            case "totalcapex", "estimated_budget" -> "totalCapex";
            default -> "createdAt"; // Default fallback
        };
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
