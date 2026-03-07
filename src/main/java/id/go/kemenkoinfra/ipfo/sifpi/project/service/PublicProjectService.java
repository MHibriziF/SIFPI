package id.go.kemenkoinfra.ipfo.sifpi.project.service;

import id.go.kemenkoinfra.ipfo.sifpi.common.dto.PagedResponseDTO;
import id.go.kemenkoinfra.ipfo.sifpi.common.enums.Sector;
import id.go.kemenkoinfra.ipfo.sifpi.project.dto.PublicProjectCatalogueDTO;

import java.math.BigDecimal;

/**
 * PM-10: Service for public project catalogue
 */
public interface PublicProjectService {
    
    /**
     * Get published projects for public catalogue (no authentication required)
     * 
     * @param sector Filter by sector (optional)
     * @param location Filter by location/province (optional)
     * @param cooperationModel Filter by funding scheme/cooperation model (optional)
     * @param minBudget Minimum budget filter (optional)
     * @param maxBudget Maximum budget filter (optional)
     * @param search Search in project name and description (optional)
     * @param page Page number (0-indexed)
     * @param size Page size (default 12)
     * @param sortBy Sort field: createdAt, name, totalCapex
     * @param sortDirection Sort direction: asc or desc
     * @return Paginated list of published projects
     */
    PagedResponseDTO<PublicProjectCatalogueDTO> getPublishedProjects(
            Sector sector,
            String location,
            String cooperationModel,
            BigDecimal minBudget,
            BigDecimal maxBudget,
            String search,
            int page,
            int size,
            String sortBy,
            String sortDirection
    );
}
