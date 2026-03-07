package id.go.kemenkoinfra.ipfo.sifpi.project.service;

import id.go.kemenkoinfra.ipfo.sifpi.common.dto.PagedResponseDTO;
import id.go.kemenkoinfra.ipfo.sifpi.common.enums.ProjectStatus;
import id.go.kemenkoinfra.ipfo.sifpi.project.dto.ProjectListItemDTO;

/**
 * Service for retrieving project owner's projects
 */
public interface ReadProjectService {
    
    /**
     * Get all projects created by the authenticated owner
     * 
     * @param status Optional filter by project status
     * @param page Page number (0-indexed)
     * @param size Number of items per page
     * @param sortBy Field to sort by (createdAt, name, etc.)
     * @param sortDirection Sort direction (asc or desc)
     * @return Paginated list of projects
     */
    PagedResponseDTO<ProjectListItemDTO> getMyProjects(
            ProjectStatus status,
            int page,
            int size,
            String sortBy,
            String sortDirection
    );
}
