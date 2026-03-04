package id.go.kemenkoinfra.ipfo.sifpi.project.mapper;

import org.mapstruct.AfterMapping;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import id.go.kemenkoinfra.ipfo.sifpi.common.services.StorageService;
import id.go.kemenkoinfra.ipfo.sifpi.project.dto.request.CreateProjectRequest;
import id.go.kemenkoinfra.ipfo.sifpi.project.dto.request.EditProjectRequest;
import id.go.kemenkoinfra.ipfo.sifpi.project.dto.ProjectResponseDTO;
import id.go.kemenkoinfra.ipfo.sifpi.project.dto.request.ProjectTimelineRequest;
import id.go.kemenkoinfra.ipfo.sifpi.project.model.Project;
import id.go.kemenkoinfra.ipfo.sifpi.project.model.ProjectSector;
import id.go.kemenkoinfra.ipfo.sifpi.project.model.ProjectStatus;
import id.go.kemenkoinfra.ipfo.sifpi.project.model.ProjectTimeline;

@Mapper(componentModel = "spring")
public interface ProjectMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "sector", expression = "java(toSector(request.getSector()))")
    @Mapping(target = "status", expression = "java(toStatus(request.getStatus()))")
    @Mapping(target = "locationImageKey", ignore = true)
    @Mapping(target = "projectStructureImageKey", ignore = true)
    @Mapping(target = "projectFileKey", ignore = true)
    @Mapping(target = "revisions", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "editedAt", ignore = true)
    Project toEntity(CreateProjectRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "sector", expression = "java(request.getSector() == null ? project.getSector() : toSector(request.getSector()))")
    @Mapping(target = "status", expression = "java(request.getStatus() == null ? project.getStatus() : toStatus(request.getStatus()))")
    @Mapping(target = "revisions", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "editedAt", ignore = true)
    void updateEntity(EditProjectRequest request, @MappingTarget Project project);

    @Mapping(target = "sector", expression = "java(fromSector(project.getSector()))")
    @Mapping(target = "status", expression = "java(fromStatus(project.getStatus()))")
    @Mapping(target = "locationImageUrl", ignore = true)
    @Mapping(target = "projectStructureImageUrl", ignore = true)
    @Mapping(target = "projectFileDownloadUrl", ignore = true)
    ProjectResponseDTO toDTO(Project project);

    default ProjectResponseDTO toDTO(Project project, StorageService storageService) {
        ProjectResponseDTO dto = toDTO(project);
        if (project == null || storageService == null) {
            return dto;
        }

        if (project.getLocationImageKey() != null && !project.getLocationImageKey().isBlank()) {
            dto.setLocationImageUrl(storageService.getPublicUrl(project.getLocationImageKey()));
        }
        if (project.getProjectStructureImageKey() != null && !project.getProjectStructureImageKey().isBlank()) {
            dto.setProjectStructureImageUrl(storageService.getPublicUrl(project.getProjectStructureImageKey()));
        }
        if (project.getProjectFileKey() != null && !project.getProjectFileKey().isBlank()) {
            dto.setProjectFileDownloadUrl(storageService.getDownloadUrl(project.getProjectFileKey()));
        }
        return dto;
    }

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "project", ignore = true)
    ProjectTimeline toTimelineEntity(ProjectTimelineRequest request);

    @AfterMapping
    default void syncProjectToTimeline(@MappingTarget Project project) {
        if (project.getTimelines() == null) {
            return;
        }

        for (ProjectTimeline timeline : project.getTimelines()) {
            timeline.setProject(project);
        }
    }

    default ProjectSector toSector(String sector) {
        if (sector == null || sector.isBlank()) {
            return null;
        }
        return ProjectSector.valueOf(sector.trim().toUpperCase());
    }

    default String fromSector(ProjectSector sector) {
        return sector == null ? null : sector.name();
    }

    default ProjectStatus toStatus(String status) {
        if (status == null || status.isBlank()) {
            return ProjectStatus.DRAFT;
        }

        String normalized = status.trim().toUpperCase().replace(" ", "_").replace("-", "_");
        return ProjectStatus.valueOf(normalized);
    }

    default String fromStatus(ProjectStatus status) {
        return status == null ? null : status.name();
    }
}
