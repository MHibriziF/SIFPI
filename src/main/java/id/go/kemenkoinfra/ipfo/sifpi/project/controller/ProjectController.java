package id.go.kemenkoinfra.ipfo.sifpi.project.controller;

import id.go.kemenkoinfra.ipfo.sifpi.common.enums.ProjectStatus;
import id.go.kemenkoinfra.ipfo.sifpi.project.dto.CatalogueExportFileDTO;
import id.go.kemenkoinfra.ipfo.sifpi.project.dto.ProjectListItemDTO;
import id.go.kemenkoinfra.ipfo.sifpi.project.service.CatalogueExportService;
import id.go.kemenkoinfra.ipfo.sifpi.project.service.ReadProjectService;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import id.go.kemenkoinfra.ipfo.sifpi.common.dto.BaseResponseDTO;
import id.go.kemenkoinfra.ipfo.sifpi.common.dto.PagedResponseDTO;
import id.go.kemenkoinfra.ipfo.sifpi.common.utils.ResponseUtil;
import id.go.kemenkoinfra.ipfo.sifpi.project.dto.ProjectResponseDTO;
import id.go.kemenkoinfra.ipfo.sifpi.project.dto.request.CatalogueExportRequest;
import id.go.kemenkoinfra.ipfo.sifpi.project.dto.request.CreateProjectRequest;
import id.go.kemenkoinfra.ipfo.sifpi.project.dto.request.EditProjectRequest;
import id.go.kemenkoinfra.ipfo.sifpi.project.service.CreateProjectService;
import id.go.kemenkoinfra.ipfo.sifpi.project.service.UpdateProjectService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
public class ProjectController {

    private final CreateProjectService createProjectService;
    private final ReadProjectService readProjectService;
    private final CatalogueExportService catalogueExportService;
    private final UpdateProjectService updateProjectService;
    private final ResponseUtil responseUtil;

    @PreAuthorize("hasAuthority('PROJECT:CREATE')")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<BaseResponseDTO<ProjectResponseDTO>> createProject(
            @Valid @RequestPart("data") CreateProjectRequest request,
            @RequestPart("mapFile") MultipartFile mapFile,
            @RequestPart("projectStructureFile") MultipartFile projectStructureFile,
            @RequestPart("projectFile") MultipartFile projectFile) {

        ProjectResponseDTO result = createProjectService.createProject(
                request,
                mapFile,
                projectStructureFile,
                projectFile);

        return responseUtil.success(result, "Proyek berhasil dibuat.", HttpStatus.CREATED);
    }

    /**
     * PM-3: Get all projects created by the authenticated project owner
     * Supports filtering by status and pagination with sorting
     */
    @PreAuthorize("hasAuthority('PROJECT:READ')")
    @GetMapping("/my-projects")
    public ResponseEntity<BaseResponseDTO<PagedResponseDTO<ProjectListItemDTO>>> getMyProjects(
            @RequestParam(required = false) ProjectStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDirection) {

        PagedResponseDTO<ProjectListItemDTO> result = readProjectService.getMyProjects(
                status,
                page,
                size,
                sortBy,
                sortDirection
        );

        return responseUtil.success(result, "Daftar proyek berhasil diambil.", HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(value = "/catalogue/export", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<byte[]> exportCatalogue(
            @Valid @RequestBody CatalogueExportRequest request) {

        CatalogueExportFileDTO result = catalogueExportService.exportCatalogue(request);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.attachment()
                        .filename(result.getFilename())
                        .build()
                        .toString())
                .body(result.getData());
    }

    /**
     * PM-5: Update project (only DRAFT projects can be edited, only by owner)
     */
    @PreAuthorize("hasAuthority('PROJECT:UPDATE')")
    @PatchMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<BaseResponseDTO<ProjectResponseDTO>> updateProject(
            @PathVariable Long id,
            @Valid @RequestPart("data") EditProjectRequest request,
            @RequestPart(value = "mapFile", required = false) MultipartFile mapFile,
            @RequestPart(value = "projectStructureFile", required = false) MultipartFile projectStructureFile,
            @RequestPart(value = "projectFile", required = false) MultipartFile projectFile) {

        ProjectResponseDTO result = updateProjectService.updateProject(
                id,
                request,
                mapFile,
                projectStructureFile,
                projectFile
        );

        return responseUtil.success(result, "Proyek berhasil diperbarui.", HttpStatus.OK);
    }
}
