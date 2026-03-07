package id.go.kemenkoinfra.ipfo.sifpi.project.service.impl;

import id.go.kemenkoinfra.ipfo.sifpi.common.dto.PagedResponseDTO;
import id.go.kemenkoinfra.ipfo.sifpi.common.enums.Sector;
import id.go.kemenkoinfra.ipfo.sifpi.common.services.StorageService;
import id.go.kemenkoinfra.ipfo.sifpi.project.dto.PublicProjectCatalogueDTO;
import id.go.kemenkoinfra.ipfo.sifpi.project.model.Project;
import id.go.kemenkoinfra.ipfo.sifpi.project.repository.ProjectRepository;
import id.go.kemenkoinfra.ipfo.sifpi.project.service.PublicProjectService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * PM-10: Implementation of public project catalogue service
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PublicProjectServiceImpl implements PublicProjectService {

    private final ProjectRepository projectRepository;
    private final StorageService storageService;

    @Override
    @Transactional(readOnly = true)
    public PagedResponseDTO<PublicProjectCatalogueDTO> getPublishedProjects(
            Sector sector,
            String location,
            String cooperationModel,
            BigDecimal minBudget,
            BigDecimal maxBudget,
            String search,
            int page,
            int size,
            String sortBy,
            String sortDirection) {

        log.info("Fetching public catalogue - sector: {}, location: '{}', cooperationModel: '{}', budget: {}-{}, search: '{}', page: {}, size: {}",
                sector, location, cooperationModel, minBudget, maxBudget, search, page, size);

        // Validate and set defaults
        if (page < 0) page = 0;
        if (size <= 0 || size > 100) size = 12; // Default 12 per page for catalogue
        if (sortBy == null || sortBy.isBlank()) sortBy = "createdAt";
        if (sortDirection == null || sortDirection.isBlank()) sortDirection = "desc";

        // Map sort field
        String entitySortField = mapSortField(sortBy);

        // Create sort and pageable
        Sort.Direction direction = sortDirection.equalsIgnoreCase("asc")
                ? Sort.Direction.ASC
                : Sort.Direction.DESC;
        Sort sort = Sort.by(direction, entitySortField);
        Pageable pageable = PageRequest.of(page, size, sort);

        // Fetch published projects
        Page<Project> projectPage = projectRepository.findPublishedProjectsForCatalogue(
                sector,
                location,
                cooperationModel,
                minBudget,
                maxBudget,
                search,
                pageable
        );

        log.info("Found {} published projects in catalogue", projectPage.getTotalElements());

        // Map to DTOs with image URLs
        List<PublicProjectCatalogueDTO> projectDTOs = projectPage.getContent().stream()
                .map(this::mapToCatalogueDTO)
                .collect(Collectors.toList());

        // Build response
        return PagedResponseDTO.<PublicProjectCatalogueDTO>builder()
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
     * Map sortBy parameter to entity field
     */
    private String mapSortField(String sortBy) {
        return switch (sortBy.toLowerCase()) {
            case "createdat", "created_at" -> "createdAt";
            case "name", "title" -> "name";
            case "totalcapex", "estimated_budget", "budget" -> "totalCapex";
            default -> "createdAt";
        };
    }

    /**
     * Map Project entity to public catalogue DTO with image URL
     */
    private PublicProjectCatalogueDTO mapToCatalogueDTO(Project project) {
        PublicProjectCatalogueDTO dto = PublicProjectCatalogueDTO.builder()
                .id(project.getId())
                .name(project.getName())
                .description(project.getDescription())
                .sector(project.getSector())
                .location(project.getLocation())
                .totalCapex(project.getTotalCapex())
                .cooperationModel(project.getCooperationModel())
                .createdAt(project.getCreatedAt())
                .build();

        // Add thumbnail/location image URL if available
        if (project.getLocationImageKey() != null && !project.getLocationImageKey().isBlank()) {
            dto.setLocationImageUrl(storageService.getPublicUrl(project.getLocationImageKey()));
        }

        return dto;
    }
}
