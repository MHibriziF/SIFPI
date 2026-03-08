package id.go.kemenkoinfra.ipfo.sifpi.project.controller;

import id.go.kemenkoinfra.ipfo.sifpi.common.dto.BaseResponseDTO;
import id.go.kemenkoinfra.ipfo.sifpi.common.dto.PagedResponseDTO;
import id.go.kemenkoinfra.ipfo.sifpi.common.enums.ProjectStatus;
import id.go.kemenkoinfra.ipfo.sifpi.common.enums.Sector;
import id.go.kemenkoinfra.ipfo.sifpi.common.utils.ResponseUtil;
import id.go.kemenkoinfra.ipfo.sifpi.project.dto.AdminProjectListItemDTO;
import id.go.kemenkoinfra.ipfo.sifpi.project.dto.BulkInsertProjectResultDTO;
import id.go.kemenkoinfra.ipfo.sifpi.project.dto.request.BulkInsertProjectRequest;
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
import org.springframework.web.bind.annotation.*;

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

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/bulk")
    public ResponseEntity<BaseResponseDTO<BulkInsertProjectResultDTO>> bulkInsertProjects(
            @RequestBody List<BulkInsertProjectRequest> requests) {

        BulkInsertProjectResultDTO result = createProjectService.bulkInsertProjects(requests);
        return responseUtil.success(result, "Bulk insert proyek selesai.", HttpStatus.CREATED);
    }
}
