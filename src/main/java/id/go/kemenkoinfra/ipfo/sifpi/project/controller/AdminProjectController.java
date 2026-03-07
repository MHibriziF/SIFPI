package id.go.kemenkoinfra.ipfo.sifpi.project.controller;

import id.go.kemenkoinfra.ipfo.sifpi.common.dto.BaseResponseDTO;
import id.go.kemenkoinfra.ipfo.sifpi.common.dto.PagedResponseDTO;
import id.go.kemenkoinfra.ipfo.sifpi.common.enums.ProjectStatus;
import id.go.kemenkoinfra.ipfo.sifpi.common.enums.Sector;
import id.go.kemenkoinfra.ipfo.sifpi.common.utils.ResponseUtil;
import id.go.kemenkoinfra.ipfo.sifpi.project.dto.AdminProjectListItemDTO;
import id.go.kemenkoinfra.ipfo.sifpi.project.service.AdminProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * PM-9: Controller for admin project management operations
 */
@RestController
@RequestMapping("/api/admin/projects")
@RequiredArgsConstructor
public class AdminProjectController {

    private final AdminProjectService adminProjectService;
    private final ResponseUtil responseUtil;

    /**
     * PM-9: Get all projects in the system with filters
     * Admin only endpoint to view, filter, and monitor all projects
     *
     * @param status Filter by project status (optional)
     * @param sector Filter by sector (optional)
     * @param ownerId Filter by owner ID (optional)
     * @param search Search term for project name or organization (optional)
     * @param page Page number (0-indexed, default 0)
     * @param size Page size (default 20, max 100)
     * @param sortBy Sort field: createdAt, updatedAt, name, totalCapex (default createdAt)
     * @param sortDirection Sort direction: asc or desc (default desc)
     * @return Paginated list of projects with owner information
     */
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<BaseResponseDTO<PagedResponseDTO<AdminProjectListItemDTO>>> getAllProjects(
            @RequestParam(required = false) ProjectStatus status,
            @RequestParam(required = false) Sector sector,
            @RequestParam(required = false) UUID ownerId,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDirection) {

        PagedResponseDTO<AdminProjectListItemDTO> result = adminProjectService.getAllProjects(
                status,
                sector,
                ownerId,
                search,
                page,
                size,
                sortBy,
                sortDirection
        );

        return responseUtil.success(result, "Daftar proyek berhasil diambil.", HttpStatus.OK);
    }
}
