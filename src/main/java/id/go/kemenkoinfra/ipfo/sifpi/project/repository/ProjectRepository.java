package id.go.kemenkoinfra.ipfo.sifpi.project.repository;

import id.go.kemenkoinfra.ipfo.sifpi.common.enums.ProjectStatus;
import id.go.kemenkoinfra.ipfo.sifpi.project.model.Project;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ProjectRepository extends JpaRepository<Project, Long> {

    /**
     * Find all projects by owner ID with pagination
     */
    Page<Project> findByOwnerId(UUID ownerId, Pageable pageable);

    /**
     * Find projects by owner ID and status with pagination
     */
    Page<Project> findByOwnerIdAndStatus(UUID ownerId, ProjectStatus status, Pageable pageable);
}
