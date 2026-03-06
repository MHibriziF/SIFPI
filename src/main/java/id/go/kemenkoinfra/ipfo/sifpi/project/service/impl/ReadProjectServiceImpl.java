package id.go.kemenkoinfra.ipfo.sifpi.project.service.impl;

import id.go.kemenkoinfra.ipfo.sifpi.common.dto.PagedResponseDTO;
import id.go.kemenkoinfra.ipfo.sifpi.common.enums.ProjectStatus;
import id.go.kemenkoinfra.ipfo.sifpi.project.dto.ProjectListItemDTO;
import id.go.kemenkoinfra.ipfo.sifpi.project.mapper.ProjectMapper;
import id.go.kemenkoinfra.ipfo.sifpi.project.model.Project;
import id.go.kemenkoinfra.ipfo.sifpi.project.repository.ProjectRepository;
import id.go.kemenkoinfra.ipfo.sifpi.project.service.ReadProjectService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReadProjectServiceImpl implements ReadProjectService {

    private final ProjectRepository projectRepository;
    private final ProjectMapper projectMapper;

    @Override
    @Transactional(readOnly = true)
    public PagedResponseDTO<ProjectListItemDTO> getMyProjects(
            UUID ownerId,
            ProjectStatus status,
            int page,
            int size,
            String sortBy,
            String sortDirection) {

        log.info("Fetching projects for owner: {}, status: {}, page: {}, size: {}, sortBy: {}, sortDirection: {}",
                ownerId, status, page, size, sortBy, sortDirection);

        // Validate and set default values
        if (page < 0) page = 0;
        if (size <= 0 || size > 100) size = 10; // Max 100 items per page
        if (sortBy == null || sortBy.isBlank()) sortBy = "createdAt";
        if (sortDirection == null || sortDirection.isBlank()) sortDirection = "desc";

        // Create sort object
        Sort.Direction direction = sortDirection.equalsIgnoreCase("asc") 
                ? Sort.Direction.ASC 
                : Sort.Direction.DESC;
        Sort sort = Sort.by(direction, sortBy);

        // Create pageable
        Pageable pageable = PageRequest.of(page, size, sort);

        // Fetch projects based on filter
        Page<Project> projectPage;
        if (status != null) {
            projectPage = projectRepository.findByOwnerIdAndStatus(ownerId, status, pageable);
            log.info("Found {} projects with status {} for owner {}", 
                    projectPage.getTotalElements(), status, ownerId);
        } else {
            projectPage = projectRepository.findByOwnerId(ownerId, pageable);
            log.info("Found {} total projects for owner {}", 
                    projectPage.getTotalElements(), ownerId);
        }

        // Map to DTOs
        List<ProjectListItemDTO> projectDTOs = projectPage.getContent().stream()
                .map(projectMapper::toListItemDTO)
                .collect(Collectors.toList());

        // Build paged response
        return PagedResponseDTO.<ProjectListItemDTO>builder()
                .content(projectDTOs)
                .page(projectPage.getNumber())
                .size(projectPage.getSize())
                .totalElements(projectPage.getTotalElements())
                .totalPages(projectPage.getTotalPages())
                .first(projectPage.isFirst())
                .last(projectPage.isLast())
                .build();
    }
}
