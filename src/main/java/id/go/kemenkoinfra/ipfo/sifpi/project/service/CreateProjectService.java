package id.go.kemenkoinfra.ipfo.sifpi.project.service;

import org.springframework.web.multipart.MultipartFile;

import id.go.kemenkoinfra.ipfo.sifpi.project.dto.ProjectResponseDTO;
import id.go.kemenkoinfra.ipfo.sifpi.project.dto.request.CreateProjectRequest;

public interface CreateProjectService {

    ProjectResponseDTO createProject(
            CreateProjectRequest request,
            MultipartFile mapFile,
            MultipartFile projectStructureFile,
            MultipartFile feasibilityStudyFile);
}
