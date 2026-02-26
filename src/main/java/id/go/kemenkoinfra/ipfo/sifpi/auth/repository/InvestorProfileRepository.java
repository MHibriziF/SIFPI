package id.go.kemenkoinfra.ipfo.sifpi.auth.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import id.go.kemenkoinfra.ipfo.sifpi.auth.model.InvestorProfile;

public interface InvestorProfileRepository extends JpaRepository<InvestorProfile, UUID> {
    Optional<InvestorProfile> findByUserId(UUID userId);
}
