package id.go.kemenkoinfra.ipfo.sifpi.project.service;

import id.go.kemenkoinfra.ipfo.sifpi.project.dto.ProjectResponseDTO;
import id.go.kemenkoinfra.ipfo.sifpi.project.dto.request.EditProjectRequest;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

/**
 * Service for updating project
 * PM-5: Update Project (PO)
 */
public interface UpdateProjectService {
    
    /**
     * Update project - only owner can update and only if status is DRAFT
     * 
     * @param projectId ID of the project to update
     * @param ownerId User ID of the owner (from JWT token)
     * @param request Update data (partial update supported)
     * @param mapFile Optional new map file
     * @param projectStructureFile Optional new project structure file
     * @param projectFile Optional new project document file
     * @return Updated project data
     */
    ProjectResponseDTO updateProject(
            Long projectId,
            UUID ownerId,
            EditProjectRequest request,
            MultipartFile mapFile,
            MultipartFile projectStructureFile,
            MultipartFile projectFile
    );
}
