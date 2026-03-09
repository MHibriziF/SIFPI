package id.go.kemenkoinfra.ipfo.sifpi.auth.repository;

import id.go.kemenkoinfra.ipfo.sifpi.auth.model.Organization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrganizationRepository extends JpaRepository<Organization, Long> {

    Optional<Organization> findByNameIgnoreCase(String name);

    @Query("SELECT o FROM Organization o WHERE LOWER(o.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) ORDER BY o.name ASC")
    List<Organization> searchByNameContains(@Param("searchTerm") String searchTerm);

    List<Organization> findAllByOrderByNameAsc();
}
