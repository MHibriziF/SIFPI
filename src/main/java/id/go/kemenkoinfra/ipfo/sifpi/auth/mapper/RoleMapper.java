package id.go.kemenkoinfra.ipfo.sifpi.auth.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import id.go.kemenkoinfra.ipfo.sifpi.auth.dto.RoleResponseDTO;
import id.go.kemenkoinfra.ipfo.sifpi.auth.dto.request.CreateRoleRequest;
import id.go.kemenkoinfra.ipfo.sifpi.auth.model.Role;
import id.go.kemenkoinfra.ipfo.sifpi.auth.model.RolePermission;

@Mapper(componentModel = "spring", uses = {PermissionsMapper.class})
public interface RoleMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "permissions", ignore = true)
    Role toEntity(CreateRoleRequest request);

    @Mapping(source = "permissions", target = "permissions")
    RoleResponseDTO toDTO(Role role, List<RolePermission> permissions);
}
