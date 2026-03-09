package id.go.kemenkoinfra.ipfo.sifpi.project.controller;

import id.go.kemenkoinfra.ipfo.sifpi.auth.repository.UserRepository;
import id.go.kemenkoinfra.ipfo.sifpi.common.dto.BaseResponseDTO;
import id.go.kemenkoinfra.ipfo.sifpi.common.dto.PagedResponseDTO;
import id.go.kemenkoinfra.ipfo.sifpi.common.enums.ProjectStatus;
import id.go.kemenkoinfra.ipfo.sifpi.common.enums.Sector;
import id.go.kemenkoinfra.ipfo.sifpi.common.utils.ResponseUtil;
import id.go.kemenkoinfra.ipfo.sifpi.project.dto.AdminProjectDetailDTO;
import id.go.kemenkoinfra.ipfo.sifpi.project.dto.AdminProjectListItemDTO;
import id.go.kemenkoinfra.ipfo.sifpi.project.dto.BulkInsertProjectResultDTO;
import id.go.kemenkoinfra.ipfo.sifpi.project.dto.ProjectResponseDTO;
import id.go.kemenkoinfra.ipfo.sifpi.project.dto.request.BulkInsertProjectRequest;
import id.go.kemenkoinfra.ipfo.sifpi.project.dto.request.BulkPublishRequest;
import id.go.kemenkoinfra.ipfo.sifpi.project.dto.request.RejectProjectRequest;
import id.go.kemenkoinfra.ipfo.sifpi.project.service.AdminProjectService;
import id.go.kemenkoinfra.ipfo.sifpi.project.service.CreateProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;

/**
 * PM-9: Controller for admin project management operations
 */
@RestController
@RequestMapping("/api/admin/projects")
@RequiredArgsConstructor
public class AdminProjectController {

    private final AdminProjectService adminProjectService;
    private final CreateProjectService createProjectService;
    private final ResponseUtil responseUtil;
    private final UserRepository userRepository;

    /**
     * PM-9: Get all projects in the system with filters
     * Admin only endpoint to view, filter, and monitor all projects
     *
     * @param status Filter by project status (optional)
     * @param sector Filter by sector (optional)
     * @param ownerId Filter by owner ID (optional)
     * @param search Search term for project name or organization (optional)
     * @param pageable Pagination parameters (page, size, sort)
     * @return Paginated list of projects with owner information
     */
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<BaseResponseDTO<PagedResponseDTO<AdminProjectListItemDTO>>> getAllProjects(
            @RequestParam(required = false) ProjectStatus status,
            @RequestParam(required = false) Sector sector,
            @RequestParam(required = false) UUID ownerId,
            @RequestParam(required = false) String search,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

        Page<AdminProjectListItemDTO> result = adminProjectService.getAllProjects(
                status,
                sector,
                ownerId,
                search,
                pageable
        );

        // Convert Page to PagedResponseDTO
        PagedResponseDTO<AdminProjectListItemDTO> pagedResponse = PagedResponseDTO.<AdminProjectListItemDTO>builder()
                .content(result.getContent())
                .page(result.getNumber())
                .size(result.getSize())
                .totalElements(result.getTotalElements())
                .totalPages(result.getTotalPages())
                .first(result.isFirst())
                .last(result.isLast())
                .build();

        return responseUtil.success(pagedResponse, "Daftar proyek berhasil diambil.", HttpStatus.OK);
    }

    /**
     * PM-6: Get single project detail by project ID
     * Admin endpoint to review detailed information and verify project completeness
     *
     * @param projectId ID of the project to retrieve
     * @return Detailed project information with owner and timeline data
     */
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<BaseResponseDTO<AdminProjectDetailDTO>> getProjectDetail(@PathVariable("id") Long projectId) {
        AdminProjectDetailDTO result = adminProjectService.getProjectDetail(projectId);
        return responseUtil.success(result, "Detail proyek berhasil diambil.", HttpStatus.OK);
    }

    /**
     * PM-7: Approve a project (single update)
     * Changes project status from IN_REVIEW to VERIFIED
     *
     * @param projectId ID of the project to approve
     * @return Updated project response
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}/approve")
    public ResponseEntity<BaseResponseDTO<ProjectResponseDTO>> approveProject(
            @PathVariable("id") Long projectId) {
        
        UUID adminId = getCurrentAdminId();
        ProjectResponseDTO result = adminProjectService.approveProject(projectId, adminId);
        return responseUtil.success(result, "Proyek berhasil disetujui.", HttpStatus.OK);
    }

    /**
     * PM-7: Reject a project (single update)
     * Changes project status from IN_REVIEW back to DRAFT
     *
     * @param projectId ID of the project to reject
     * @param request Request containing rejection reason
     * @return Updated project response
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}/reject")
    public ResponseEntity<BaseResponseDTO<ProjectResponseDTO>> rejectProject(
            @PathVariable("id") Long projectId,
            @Valid @RequestBody RejectProjectRequest request) {
        
        UUID adminId = getCurrentAdminId();
        ProjectResponseDTO result = adminProjectService.rejectProject(projectId, request, adminId);
        return responseUtil.success(result, "Proyek berhasil ditolak.", HttpStatus.OK);
    }

    /**
     * PM-7: Bulk publish projects
     * Changes multiple projects status from APPROVED to PUBLISHED
     *
     * @param request Request containing list of project IDs to publish
     * @return Count of successfully published projects
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/bulk-publish")
    public ResponseEntity<BaseResponseDTO<Integer>> bulkPublishProjects(
            @Valid @RequestBody BulkPublishRequest request) {
        
        UUID adminId = getCurrentAdminId();
        int publishedCount = adminProjectService.bulkPublishProjects(request, adminId);
        return responseUtil.success(publishedCount, publishedCount + " proyek berhasil dipublikasikan.", HttpStatus.OK);
    }

    /**
     * PM-7: Bulk unpublish projects
     * Changes multiple projects status from PUBLISHED back to APPROVED
     *
     * @param request Request containing list of project IDs to unpublish
     * @return Count of successfully unpublished projects
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/bulk-unpublish")
    public ResponseEntity<BaseResponseDTO<Integer>> bulkUnpublishProjects(
            @Valid @RequestBody BulkPublishRequest request) {
        
        UUID adminId = getCurrentAdminId();
        int unpublishedCount = adminProjectService.bulkUnpublishProjects(request, adminId);
        return responseUtil.success(unpublishedCount, unpublishedCount + " proyek berhasil di-unpublish.", HttpStatus.OK);
    }

    /**
     * Resolve current authenticated admin user's UUID from email
     */
    private UUID getCurrentAdminId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        return userRepository.findByEmail(email)
                .map(user -> user.getId())
                .orElseThrow(() -> new IllegalStateException("Admin user tidak ditemukan"));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/batch-upload")
    public ResponseEntity<BaseResponseDTO<BulkInsertProjectResultDTO>> bulkInsertProjects(
            @RequestBody List<BulkInsertProjectRequest> requests) {

        BulkInsertProjectResultDTO result = createProjectService.bulkInsertProjects(requests);
        return responseUtil.success(result, "Bulk insert proyek selesai.", HttpStatus.CREATED);
    }
}
