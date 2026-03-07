package id.go.kemenkoinfra.ipfo.sifpi.project.service;

import id.go.kemenkoinfra.ipfo.sifpi.common.dto.PagedResponseDTO;
import id.go.kemenkoinfra.ipfo.sifpi.common.enums.ProjectStatus;
import id.go.kemenkoinfra.ipfo.sifpi.common.enums.Sector;
import id.go.kemenkoinfra.ipfo.sifpi.project.dto.AdminProjectListItemDTO;

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
     * @param page Page number (0-indexed)
     * @param size Number of items per page (default 20)
     * @param sortBy Field to sort by (createdAt, updatedAt, name, totalCapex)
     * @param sortDirection Sort direction (asc or desc)
     * @return Paginated list of projects with owner information
     */
    PagedResponseDTO<AdminProjectListItemDTO> getAllProjects(
            ProjectStatus status,
            Sector sector,
            UUID ownerId,
            String search,
            int page,
            int size,
            String sortBy,
            String sortDirection
    );
}
