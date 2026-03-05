package id.go.kemenkoinfra.ipfo.sifpi.project.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import id.go.kemenkoinfra.ipfo.sifpi.common.dto.BaseResponseDTO;
import id.go.kemenkoinfra.ipfo.sifpi.common.utils.ResponseUtil;
import id.go.kemenkoinfra.ipfo.sifpi.project.dto.ProjectResponseDTO;
import id.go.kemenkoinfra.ipfo.sifpi.project.dto.request.CreateProjectRequest;
import id.go.kemenkoinfra.ipfo.sifpi.project.service.ProjectService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;
    private final ResponseUtil responseUtil;

    @PreAuthorize("hasAnyRole('PROJECT_OWNER','ADMIN')")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<BaseResponseDTO<ProjectResponseDTO>> createProject(
            @Valid @RequestPart("data") CreateProjectRequest request,
            @RequestPart("mapFile") MultipartFile mapFile,
            @RequestPart("projectStructureFile") MultipartFile projectStructureFile,
            @RequestPart("projectFile") MultipartFile projectFile) {

        ProjectResponseDTO result = projectService.createProject(
                request,
                mapFile,
                projectStructureFile,
                projectFile);

        return responseUtil.success(result, "Proyek berhasil dibuat.", HttpStatus.CREATED);
    }
}
