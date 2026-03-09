package id.go.kemenkoinfra.ipfo.sifpi.project.repository;

import id.go.kemenkoinfra.ipfo.sifpi.project.model.ProjectStatusHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for ProjectStatusHistory entity
 */
@Repository
public interface ProjectStatusHistoryRepository extends JpaRepository<ProjectStatusHistory, Long> {
    /**
     * Find all status histories for a specific project
     * @param projectId the project ID
     * @return list of status histories ordered by changed date descending
     */
    List<ProjectStatusHistory> findByProjectIdOrderByChangedAtDesc(Long projectId);
}
