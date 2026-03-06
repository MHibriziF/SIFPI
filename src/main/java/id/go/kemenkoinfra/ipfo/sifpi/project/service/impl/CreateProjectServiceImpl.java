package id.go.kemenkoinfra.ipfo.sifpi.project.service.impl;

import java.util.List;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import id.go.kemenkoinfra.ipfo.sifpi.auth.model.User;
import id.go.kemenkoinfra.ipfo.sifpi.auth.repository.UserRepository;
import id.go.kemenkoinfra.ipfo.sifpi.common.services.StorageService;
import id.go.kemenkoinfra.ipfo.sifpi.project.dto.ProjectResponseDTO;
import id.go.kemenkoinfra.ipfo.sifpi.project.dto.request.CreateProjectRequest;
import id.go.kemenkoinfra.ipfo.sifpi.project.mapper.ProjectMapper;
import id.go.kemenkoinfra.ipfo.sifpi.project.model.Project;
import id.go.kemenkoinfra.ipfo.sifpi.project.model.ProjectTimeline;
import id.go.kemenkoinfra.ipfo.sifpi.project.repository.ProjectRepository;
import id.go.kemenkoinfra.ipfo.sifpi.project.service.CreateProjectService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CreateProjectServiceImpl implements CreateProjectService {

    private static final String STORAGE_FOLDER = "projects";

    private final ProjectRepository projectRepository;
    private final ProjectMapper projectMapper;
    private final StorageService storageService;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public ProjectResponseDTO createProject(
            CreateProjectRequest request,
            MultipartFile mapFile,
            MultipartFile projectStructureFile,
            MultipartFile projectFile) {
        User creator = resolveCurrentUser();
        validateFinancialMetrics(request);

        String locationImageKey = storageService.upload(STORAGE_FOLDER, mapFile);
        String projectStructureImageKey = storageService.upload(STORAGE_FOLDER, projectStructureFile);
        String projectFileKey = storageService.upload(STORAGE_FOLDER, projectFile);

        Project project = projectMapper.toEntity(request);
        project.setLocationImageKey(locationImageKey);
        project.setProjectStructureImageKey(projectStructureImageKey);
        project.setProjectFileKey(projectFileKey);
        project.setOwnerId(creator.getId());

        attachProjectToTimelines(project);

        Project savedProject = projectRepository.save(project);
        return projectMapper.toDTO(savedProject, storageService);
    }

    private void attachProjectToTimelines(Project project) {
        List<ProjectTimeline> timelines = project.getTimelines();
        if (timelines == null || timelines.isEmpty()) {
            return;
        }

        for (ProjectTimeline timeline : timelines) {
            timeline.setProject(project);
        }
    }

    private void validateFinancialMetrics(CreateProjectRequest request) {
        if (!Boolean.TRUE.equals(request.getIsFeasibilityStudy())) {
            return;
        }

        if (request.getTotalCapex() == null) {
            throw new IllegalArgumentException("totalCapex wajib diisi ketika isFeasibilityStudy bernilai true");
        }
        if (request.getTotalOpex() == null) {
            throw new IllegalArgumentException("totalOpex wajib diisi ketika isFeasibilityStudy bernilai true");
        }
        if (request.getNpv() == null) {
            throw new IllegalArgumentException("npv wajib diisi ketika isFeasibilityStudy bernilai true");
        }
        if (request.getIrr() == null) {
            throw new IllegalArgumentException("irr wajib diisi ketika isFeasibilityStudy bernilai true");
        }
    }

    private User resolveCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AccessDeniedException("Akses ditolak.");
        }

        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AccessDeniedException("Akses ditolak."));

        if (!user.isEmailVerified()) {
            throw new AccessDeniedException("User belum terverifikasi.");
        }

        return user;
    }
}
