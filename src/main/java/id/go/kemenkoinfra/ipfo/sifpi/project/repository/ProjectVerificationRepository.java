package id.go.kemenkoinfra.ipfo.sifpi.project.repository;

import id.go.kemenkoinfra.ipfo.sifpi.project.model.ProjectVerification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for ProjectVerification entity
 */
@Repository
public interface ProjectVerificationRepository extends JpaRepository<ProjectVerification, Long> {
    /**
     * Find all verifications for a specific project
     * @param projectId the project ID
     * @return list of verifications ordered by verification date descending
     */
    List<ProjectVerification> findByProjectIdOrderByVerifiedAtDesc(Long projectId);
}
