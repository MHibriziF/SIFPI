package id.go.kemenkoinfra.ipfo.sifpi.auth.repository;

import id.go.kemenkoinfra.ipfo.sifpi.auth.model.PendingRegistration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PendingRegistrationRepository extends JpaRepository<PendingRegistration, UUID> {
    Optional<PendingRegistration> findByEmail(String email);
    
    Optional<PendingRegistration> findByEmailVerificationToken(String token);
    
    boolean existsByEmail(String email);
}
