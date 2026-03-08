package id.go.kemenkoinfra.ipfo.sifpi.project.repository;

import id.go.kemenkoinfra.ipfo.sifpi.common.enums.ProjectStatus;
import id.go.kemenkoinfra.ipfo.sifpi.common.enums.Sector;
import id.go.kemenkoinfra.ipfo.sifpi.project.model.Project;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.math.BigDecimal;
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

    @EntityGraph(attributePaths = {"timelines", "revisions"})
    List<Project> findAllByIdIn(List<Long> ids);

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

    /**
     * PM-10: Find published projects for public catalogue
     * Only returns TERPUBLIKASI projects with optional filters
     */
    @Query("SELECT p FROM Project p WHERE " +
            "p.status = 'TERPUBLIKASI' AND " +
            "(:sector IS NULL OR p.sector = :sector) AND " +
            "(:location IS NULL OR :location = '' OR " +
            "LOWER(p.location) LIKE LOWER(CONCAT('%', :location, '%'))) AND " +
            "(:cooperationModel IS NULL OR :cooperationModel = '' OR " +
            "LOWER(p.cooperationModel) LIKE LOWER(CONCAT('%', :cooperationModel, '%'))) AND " +
            "(:minBudget IS NULL OR p.totalCapex >= :minBudget) AND " +
            "(:maxBudget IS NULL OR p.totalCapex <= :maxBudget) AND " +
            "(:search IS NULL OR :search = '' OR " +
            "LOWER(p.name) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(p.description) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<Project> findPublishedProjectsForCatalogue(
            @Param("sector") Sector sector,
            @Param("location") String location,
            @Param("cooperationModel") String cooperationModel,
            @Param("minBudget") BigDecimal minBudget,
            @Param("maxBudget") BigDecimal maxBudget,
            @Param("search") String search,
            Pageable pageable
    );
}
