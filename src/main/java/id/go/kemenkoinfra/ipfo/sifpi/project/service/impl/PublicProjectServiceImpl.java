package id.go.kemenkoinfra.ipfo.sifpi.project.service.impl;

import id.go.kemenkoinfra.ipfo.sifpi.common.enums.Sector;
import id.go.kemenkoinfra.ipfo.sifpi.common.services.StorageService;
import id.go.kemenkoinfra.ipfo.sifpi.project.dto.PublicProjectCatalogueDTO;
import id.go.kemenkoinfra.ipfo.sifpi.project.model.Project;
import id.go.kemenkoinfra.ipfo.sifpi.project.repository.ProjectRepository;
import id.go.kemenkoinfra.ipfo.sifpi.project.service.PublicProjectService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

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
    public Page<PublicProjectCatalogueDTO> getPublishedProjects(
            Sector sector,
            String location,
            String cooperationModel,
            BigDecimal minBudget,
            BigDecimal maxBudget,
            String search,
            Pageable pageable) {

        log.info("Fetching public catalogue - sector: {}, location: '{}', cooperationModel: '{}', budget: {}-{}, search: '{}', pageable: {}",
                sector, location, cooperationModel, minBudget, maxBudget, search, pageable);

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
        return projectPage.map(this::mapToCatalogueDTO);
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
