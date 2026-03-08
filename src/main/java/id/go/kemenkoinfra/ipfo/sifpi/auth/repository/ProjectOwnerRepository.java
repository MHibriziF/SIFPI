package id.go.kemenkoinfra.ipfo.sifpi.auth.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import id.go.kemenkoinfra.ipfo.sifpi.auth.model.ProjectOwnerProfile;

@Repository
public interface ProjectOwnerRepository extends JpaRepository<ProjectOwnerProfile, UUID> {
    
    Optional<ProjectOwnerProfile> findByUserEmail(String email);
}
