package id.go.kemenkoinfra.ipfo.sifpi.auth.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import id.go.kemenkoinfra.ipfo.sifpi.auth.model.Resource;
import id.go.kemenkoinfra.ipfo.sifpi.auth.model.Role;
import id.go.kemenkoinfra.ipfo.sifpi.auth.model.RolePermission;
import id.go.kemenkoinfra.ipfo.sifpi.common.enums.Action;

public interface RolePermissionRepository extends JpaRepository<RolePermission, UUID> {

    boolean existsByRoleAndResourceAndAction(Role role, Resource resource, Action action);

    @Query("SELECT rp FROM RolePermission rp JOIN FETCH rp.resource WHERE rp.role = :role")
    List<RolePermission> findByRoleWithResource(@Param("role") Role role);

    void deleteByRole(Role role);
}
