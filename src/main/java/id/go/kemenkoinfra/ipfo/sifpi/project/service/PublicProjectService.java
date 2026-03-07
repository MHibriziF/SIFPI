package id.go.kemenkoinfra.ipfo.sifpi.project.service;

import id.go.kemenkoinfra.ipfo.sifpi.common.enums.Sector;
import id.go.kemenkoinfra.ipfo.sifpi.project.dto.PublicProjectCatalogueDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

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
     * @param pageable Pagination and sorting parameters
     * @return Page of published projects
     */
    Page<PublicProjectCatalogueDTO> getPublishedProjects(
            Sector sector,
            String location,
            String cooperationModel,
            BigDecimal minBudget,
            BigDecimal maxBudget,
            String search,
            Pageable pageable
    );
}
