package id.go.kemenkoinfra.ipfo.sifpi.auth.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import id.go.kemenkoinfra.ipfo.sifpi.auth.model.Role;
import id.go.kemenkoinfra.ipfo.sifpi.auth.model.RoleAuditLog;

public interface RoleAuditLogRepository extends JpaRepository<RoleAuditLog, UUID> {
    List<RoleAuditLog> findByRoleOrderByTimestampDesc(Role role);
}
