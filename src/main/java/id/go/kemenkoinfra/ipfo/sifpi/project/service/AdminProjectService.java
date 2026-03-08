package id.go.kemenkoinfra.ipfo.sifpi.project.service;

import id.go.kemenkoinfra.ipfo.sifpi.common.enums.ProjectStatus;
import id.go.kemenkoinfra.ipfo.sifpi.common.enums.Sector;
import id.go.kemenkoinfra.ipfo.sifpi.project.dto.AdminProjectListItemDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

/**
 * PM-9: Service for admin project management operations
 */
public interface AdminProjectService {
    
    /**
     * Get all projects in the system with optional filters
     * 
     * @param status Optional filter by project status
     * @param sector Optional filter by sector
     * @param ownerId Optional filter by owner ID
     * @param search Optional search term (matches name or organization)
     * @param pageable Pagination and sorting parameters
     * @return Page of projects with owner information
     */
    Page<AdminProjectListItemDTO> getAllProjects(
            ProjectStatus status,
            Sector sector,
            UUID ownerId,
            String search,
            Pageable pageable
    );
}
