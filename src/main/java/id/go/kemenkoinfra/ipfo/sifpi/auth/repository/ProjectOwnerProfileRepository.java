package id.go.kemenkoinfra.ipfo.sifpi.auth.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import id.go.kemenkoinfra.ipfo.sifpi.auth.model.ProjectOwnerProfile;

public interface ProjectOwnerProfileRepository extends JpaRepository<ProjectOwnerProfile, UUID> {
    Optional<ProjectOwnerProfile> findByUserId(UUID userId);
}
