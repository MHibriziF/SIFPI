package id.go.kemenkoinfra.ipfo.sifpi.auth.service;

import java.util.UUID;

import id.go.kemenkoinfra.ipfo.sifpi.auth.dto.request.CreateRoleRequest;
import id.go.kemenkoinfra.ipfo.sifpi.auth.dto.RoleResponseDTO;

public interface RoleService {
    RoleResponseDTO createRole(CreateRoleRequest request);
    RoleResponseDTO getRoleById(UUID id);
}
