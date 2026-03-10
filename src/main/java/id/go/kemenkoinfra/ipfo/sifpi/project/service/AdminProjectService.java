package id.go.kemenkoinfra.ipfo.sifpi.project.service;

import id.go.kemenkoinfra.ipfo.sifpi.common.enums.ProjectStatus;
import id.go.kemenkoinfra.ipfo.sifpi.common.enums.Sector;
import id.go.kemenkoinfra.ipfo.sifpi.project.dto.AdminProjectDetailDTO;
import id.go.kemenkoinfra.ipfo.sifpi.project.dto.AdminProjectListItemDTO;
import id.go.kemenkoinfra.ipfo.sifpi.project.dto.ProjectResponseDTO;
import id.go.kemenkoinfra.ipfo.sifpi.project.dto.request.RejectProjectRequest;
import id.go.kemenkoinfra.ipfo.sifpi.project.dto.request.BulkPublishRequest;
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
    
    /**
     * PM-6: Get single project detail by project ID
     * Admin can review detailed information including verification data
     * 
     * @param projectId ID of the project to retrieve
     * @return Detailed project information with owner and timeline data
     */
    AdminProjectDetailDTO getProjectDetail(Long projectId);

    /**
     * PM-7: Approve a project (single update)
     * Changes project status from IN_REVIEW to VERIFIED
     * 
     * @param projectId ID of the project to approve
     * @param adminId ID of the admin performing the action
     * @return Updated project response
     */
    ProjectResponseDTO approveProject(Long projectId, UUID adminId);

    /**
     * PM-7: Reject a project (single update)
     * Changes project status from DIAJUKAN or IN_REVIEW to PERBAIKAN_DATA
     * Records rejection reason in verification records
     * 
     * @param projectId ID of the project to reject
     * @param request Request containing rejection reason
     * @param adminId ID of the admin performing the action
     * @return Updated project response
     */
    ProjectResponseDTO rejectProject(Long projectId, RejectProjectRequest request, UUID adminId);

    /**
     * PM-7: Bulk publish projects
     * Changes multiple projects status from APPROVED to PUBLISHED
     * 
     * @param request Request containing list of project IDs to publish
     * @param adminId ID of the admin performing the action
     * @return Count of successfully published projects
     */
    int bulkPublishProjects(BulkPublishRequest request, UUID adminId);

    /**
     * PM-7: Bulk unpublish projects
     * Changes multiple projects status from PUBLISHED back to APPROVED
     * 
     * @param request Request containing list of project IDs to unpublish
     * @param adminId ID of the admin performing the action
     * @return Count of successfully unpublished projects
     */
    int bulkUnpublishProjects(BulkPublishRequest request, UUID adminId);
}
