package id.go.kemenkoinfra.ipfo.sifpi.auth.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import id.go.kemenkoinfra.ipfo.sifpi.auth.model.User;

public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmailAndDeletedAtIsNull(String email);
    Optional<User> findByIdAndDeletedAtIsNull(UUID id);
    Page<User> findAllByDeletedAtIsNull(Pageable pageable);
    boolean existsByEmail(String email);

    // Single query — caller controls sort via PageRequest.of(page, size, Sort.by(...))
    @Query(value = "SELECT u FROM User u JOIN u.role r WHERE u.deletedAt IS NULL " +
           "AND (:role IS NULL OR r.name = :role) " +
           "AND (:isVerified IS NULL OR u.isVerified = :isVerified) " +
           "AND (:isActive IS NULL OR u.isActive = :isActive) " +
           "AND (:organisasi IS NULL OR LOWER(u.organisasi) = :organisasi) " +
           "AND (LOWER(u.name) LIKE LOWER(CONCAT('%', :search, '%')) " +
           "OR LOWER(u.email) LIKE LOWER(CONCAT('%', :search, '%')) " +
           "OR COALESCE(LOWER(u.organisasi), '') LIKE LOWER(CONCAT('%', :search, '%')))",
           countQuery = "SELECT COUNT(u) FROM User u JOIN u.role r WHERE u.deletedAt IS NULL " +
           "AND (:role IS NULL OR r.name = :role) " +
           "AND (:isVerified IS NULL OR u.isVerified = :isVerified) " +
           "AND (:isActive IS NULL OR u.isActive = :isActive) " +
           "AND (:organisasi IS NULL OR LOWER(u.organisasi) = :organisasi) " +
           "AND (LOWER(u.name) LIKE LOWER(CONCAT('%', :search, '%')) " +
           "OR LOWER(u.email) LIKE LOWER(CONCAT('%', :search, '%')) " +
           "OR COALESCE(LOWER(u.organisasi), '') LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<User> findAllByFilters(
        @Param("role") String role,
        @Param("isVerified") Boolean isVerified,
        @Param("isActive") Boolean isActive,
        @Param("organisasi") String organisasi,
        @Param("search") String search,
        Pageable pageable
    );
    
    // For password management
    Optional<User> findByEmail(String email);
    Optional<User> findByPasswordSetupToken(String token);
}
