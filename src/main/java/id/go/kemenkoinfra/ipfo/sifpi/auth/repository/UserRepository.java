package id.go.kemenkoinfra.ipfo.sifpi.auth.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import id.go.kemenkoinfra.ipfo.sifpi.auth.model.User;

public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmailAndDeletedAtIsNull(String email);
    Optional<User> findByIdAndDeletedAtIsNull(UUID id);
    Page<User> findAllByDeletedAtIsNull(Pageable pageable);
    boolean existsByEmail(String email);
}
