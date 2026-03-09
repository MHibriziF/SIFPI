package id.go.kemenkoinfra.ipfo.sifpi.project.service;

import id.go.kemenkoinfra.ipfo.sifpi.project.dto.ProjectResponseDTO;
import id.go.kemenkoinfra.ipfo.sifpi.project.dto.ProjectStatusHistoryDTO;

import java.util.List;

/**
 * Service for PM-4: detailed single-project view for the project owner.
 */
public interface ReadProjectDetailService {

    /**
     * Returns the full detail of a project.
     * Enforces RLS: the caller must be the project owner, otherwise 403.
     *
     * @param id project primary key
     * @return full project detail DTO
     */
    ProjectResponseDTO getProjectDetail(Long id);

    /**
     * Returns the status-transition history of a project.
     * Enforces RLS: the caller must be the project owner, otherwise 403.
     *
     * @param id project primary key
     * @return ordered list of status-history entries, or null if not yet implemented
     */
    List<ProjectStatusHistoryDTO> getProjectHistory(Long id);
}
