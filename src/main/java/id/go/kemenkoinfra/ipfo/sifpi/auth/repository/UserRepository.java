package id.go.kemenkoinfra.ipfo.sifpi.auth.repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import id.go.kemenkoinfra.ipfo.sifpi.auth.model.User;

public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByEmail(String email);
    Optional<User> findById(UUID id);

    Page<User> findAll(Pageable pageable);

    boolean existsByEmail(String email);

    @Query("select lower(u.email) from User u where lower(u.email) in :emails")
    List<String> findExistingEmailsIgnoreCase(@Param("emails") Collection<String> emails);

// ── UM-6: single filter query — caller controls ORDER BY via Pageable ──────

    // Sort options (use as PageRequest.of(page, size, Sort.by(...))):
    //   default          → Sort.by("createdAt").descending()
    //   sortBy=status    → Sort.by("active").[ascending|descending]
    //   sortBy=role      → Sort.by("role.name").[ascending|descending]
    @Query(value = "SELECT u FROM User u JOIN FETCH u.role r "
            + "WHERE (:role IS NULL OR r.name = :role) "
            + "AND (:isVerified IS NULL OR u.emailVerified = :isVerified) "
            + "AND (:isActive IS NULL OR u.active = :isActive) "
            + "AND (:organisasi IS NULL OR LOWER(u.organization) = :organisasi) "
            + "AND ('' = :search "
            + "     OR LOWER(u.name) LIKE LOWER(CONCAT('%', :search, '%')) "
            + "     OR LOWER(u.email) LIKE LOWER(CONCAT('%', :search, '%')) "
            + "     OR COALESCE(LOWER(u.organization), '') LIKE LOWER(CONCAT('%', :search, '%')))",
            countQuery = "SELECT COUNT(u) FROM User u JOIN u.role r "
            + "WHERE (:role IS NULL OR r.name = :role) "
            + "AND (:isVerified IS NULL OR u.emailVerified = :isVerified) "
            + "AND (:isActive IS NULL OR u.active = :isActive) "
            + "AND (:organisasi IS NULL OR LOWER(u.organization) = :organisasi) "
            + "AND ('' = :search "
            + "     OR LOWER(u.name) LIKE LOWER(CONCAT('%', :search, '%')) "
            + "     OR LOWER(u.email) LIKE LOWER(CONCAT('%', :search, '%')) "
            + "     OR COALESCE(LOWER(u.organization), '') LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<User> findAllByFilters(
            @Param("role") String role,
            @Param("isVerified") Boolean isVerified,
            @Param("isActive") Boolean isActive,
            @Param("organisasi") String organisasi,
            @Param("search") String search,
            Pageable pageable);
}
