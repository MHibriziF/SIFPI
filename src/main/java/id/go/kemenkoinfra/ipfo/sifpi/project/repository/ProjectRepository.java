package id.go.kemenkoinfra.ipfo.sifpi.project.repository;

import id.go.kemenkoinfra.ipfo.sifpi.common.enums.ProjectStatus;
import id.go.kemenkoinfra.ipfo.sifpi.common.enums.Sector;
import id.go.kemenkoinfra.ipfo.sifpi.project.model.Project;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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

    /**
     * PM-9: Find all projects with optional filters for admin
     * Supports filtering by status, sector, owner ID, and search term
     */
    @Query("SELECT p FROM Project p WHERE " +
            "(:status IS NULL OR p.status = :status) AND " +
            "(:sector IS NULL OR p.sector = :sector) AND " +
            "(:ownerId IS NULL OR p.ownerId = :ownerId) AND " +
            "(:search IS NULL OR :search = '' OR " +
            "LOWER(p.name) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(p.ownerInstitution) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<Project> findAllWithFilters(
            @Param("status") ProjectStatus status,
            @Param("sector") Sector sector,
            @Param("ownerId") UUID ownerId,
            @Param("search") String search,
            Pageable pageable
    );
}
