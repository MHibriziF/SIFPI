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

    @Query("SELECT u FROM User u WHERE u.role.name = 'PROJECT_OWNER' AND u.verified = false AND u.emailVerified = true ORDER BY u.createdAt ASC")
    Page<User> findUnverifiedProjectOwners(Pageable pageable);
}
