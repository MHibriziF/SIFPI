package id.go.kemenkoinfra.ipfo.sifpi.project.service.impl;

import id.go.kemenkoinfra.ipfo.sifpi.auth.model.User;
import id.go.kemenkoinfra.ipfo.sifpi.auth.repository.UserRepository;
import id.go.kemenkoinfra.ipfo.sifpi.common.enums.ProjectStatus;
import id.go.kemenkoinfra.ipfo.sifpi.common.exception.ForbiddenException;
import id.go.kemenkoinfra.ipfo.sifpi.common.exception.NotFoundException;
import id.go.kemenkoinfra.ipfo.sifpi.common.services.StorageService;
import id.go.kemenkoinfra.ipfo.sifpi.project.dto.ProjectResponseDTO;
import id.go.kemenkoinfra.ipfo.sifpi.project.dto.request.EditProjectRequest;
import id.go.kemenkoinfra.ipfo.sifpi.project.mapper.ProjectMapper;
import id.go.kemenkoinfra.ipfo.sifpi.project.model.Project;
import id.go.kemenkoinfra.ipfo.sifpi.project.model.ProjectTimeline;
import id.go.kemenkoinfra.ipfo.sifpi.project.repository.ProjectRepository;
import id.go.kemenkoinfra.ipfo.sifpi.project.service.UpdateProjectService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UpdateProjectServiceImpl implements UpdateProjectService {

    private final ProjectRepository projectRepository;
    private final ProjectMapper projectMapper;
    private final StorageService storageService;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public ProjectResponseDTO updateProject(
            Long projectId,
            EditProjectRequest request,
            MultipartFile mapFile,
            MultipartFile projectStructureFile,
            MultipartFile projectFile) {

        // Resolve current user from SecurityContext
        User currentUser = resolveCurrentUser();
        UUID ownerId = currentUser.getId();

        log.info("Updating project ID: {} by owner: {}", projectId, ownerId);

        // 1. Find project
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new NotFoundException("Project", projectId.toString()));

        // 2. Validate owner
        if (!project.getOwnerId().equals(ownerId)) {
            log.warn("User {} attempted to edit project {} owned by {}", ownerId, projectId, project.getOwnerId());
            throw new ForbiddenException("Anda tidak memiliki akses untuk mengedit proyek ini");
        }

        // 3. Validate status - only DRAFT can be edited
        if (project.getStatus() != ProjectStatus.DRAFT) {
            log.warn("Attempt to edit project {} with status {}", projectId, project.getStatus());
            throw new ForbiddenException("Hanya proyek dengan status DRAFT yang dapat diedit");
        }

        // 4. Upload new files if provided (optional)
        if (mapFile != null && !mapFile.isEmpty()) {
            log.info("Uploading new map file for project {}", projectId);
            // Delete old file
            if (project.getLocationImageKey() != null) {
                storageService.delete(project.getLocationImageKey());
            }
            String newKey = storageService.upload("projects/maps", mapFile);
            request.setLocationImageKey(newKey);
        }

        if (projectStructureFile != null && !projectStructureFile.isEmpty()) {
            log.info("Uploading new project structure file for project {}", projectId);
            // Delete old file
            if (project.getProjectStructureImageKey() != null) {
                storageService.delete(project.getProjectStructureImageKey());
            }
            String newKey = storageService.upload("projects/structures", projectStructureFile);
            request.setProjectStructureImageKey(newKey);
        }

        if (projectFile != null && !projectFile.isEmpty()) {
            log.info("Uploading new project document for project {}", projectId);
            // Delete old file
            if (project.getProjectFileKey() != null) {
                storageService.delete(project.getProjectFileKey());
            }
            String newKey = storageService.upload("projects/documents", projectFile);
            request.setProjectFileKey(newKey);
        }

        // 5. Update timelines (upsert: update/insert/delete)
        if (request.getTimelines() != null) {
            log.info("Updating timelines for project {}", projectId);
            updateTimelines(project, request);
        }

        // 6. Update project fields (partial update using MapStruct)
        projectMapper.updateEntity(request, project);

        // 7. Save project
        Project updatedProject = projectRepository.save(project);
        log.info("Project {} updated successfully", projectId);

        // 8. Return DTO with file URLs
        return projectMapper.toDTO(updatedProject, storageService);
    }

    /**
     * Update timelines with upsert logic:
     * - Keep existing timelines from request
     * - Remove timelines not in request
     * - Add new timelines from request
     */
    private void updateTimelines(Project project, EditProjectRequest request) {
        // Clear existing timelines
        if (project.getTimelines() == null) {
            project.setTimelines(new ArrayList<>());
        } else {
            project.getTimelines().clear();
        }

        // Add new timelines from request
        if (request.getTimelines() != null && !request.getTimelines().isEmpty()) {
            List<ProjectTimeline> newTimelines = request.getTimelines().stream()
                    .map(timelineRequest -> {
                        ProjectTimeline timeline = projectMapper.toTimelineEntity(timelineRequest);
                        timeline.setProject(project);
                        return timeline;
                    })
                    .toList();
            
            project.getTimelines().addAll(newTimelines);
            log.info("Added {} timelines to project", newTimelines.size());
        }
    }

    /**
     * Resolve current user from SecurityContext
     */
    private User resolveCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AccessDeniedException("Akses ditolak.");
        }

        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AccessDeniedException("Akses ditolak."));

        if (!user.isEmailVerified()) {
            throw new AccessDeniedException("Akses ditolak.");
        }

        return user;
    }
}
