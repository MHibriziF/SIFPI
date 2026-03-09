package id.go.kemenkoinfra.ipfo.sifpi.auth.service;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import id.go.kemenkoinfra.ipfo.sifpi.auth.dto.RoleResponseDTO;
import id.go.kemenkoinfra.ipfo.sifpi.auth.dto.RoleSummaryDTO;
import id.go.kemenkoinfra.ipfo.sifpi.auth.dto.RoleUserDTO;
import id.go.kemenkoinfra.ipfo.sifpi.auth.dto.request.CreateRoleRequest;
import id.go.kemenkoinfra.ipfo.sifpi.auth.dto.request.UpdateRoleRequest;

public interface RoleService {
    List<RoleSummaryDTO> getAllRoles();
    Page<RoleUserDTO> getUsersByRole(UUID roleId, String search, Pageable pageable);
    RoleResponseDTO createRole(CreateRoleRequest request);
    RoleResponseDTO getRoleById(UUID id);
    RoleResponseDTO updateRole(UUID id, UpdateRoleRequest request);
}
