package id.go.kemenkoinfra.ipfo.sifpi.auth.mapper;

import java.util.List;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import id.go.kemenkoinfra.ipfo.sifpi.auth.dto.RoleResponseDTO;
import id.go.kemenkoinfra.ipfo.sifpi.auth.dto.RoleSummaryDTO;
import id.go.kemenkoinfra.ipfo.sifpi.auth.dto.RoleUserDTO;
import id.go.kemenkoinfra.ipfo.sifpi.auth.dto.request.CreateRoleRequest;
import id.go.kemenkoinfra.ipfo.sifpi.auth.dto.request.UpdateRoleRequest;
import id.go.kemenkoinfra.ipfo.sifpi.auth.model.Role;
import id.go.kemenkoinfra.ipfo.sifpi.auth.model.RolePermission;
import id.go.kemenkoinfra.ipfo.sifpi.auth.model.User;

@Mapper(componentModel = "spring", uses = {PermissionsMapper.class})
public interface RoleMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "permissions", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Role toEntity(CreateRoleRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "permissions", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntity(UpdateRoleRequest request, @MappingTarget Role role);

    @Mapping(source = "permissions", target = "permissions")
    RoleResponseDTO toDTO(Role role, List<RolePermission> permissions);

    @Mapping(target = "userCount", ignore = true)
    RoleSummaryDTO toSummaryDTO(Role role);

    @Mapping(source = "name", target = "nama")
    @Mapping(target = "organisasi", ignore = true)
    @Mapping(target = "sectorInterest", ignore = true)
    @Mapping(target = "budgetRange", ignore = true)
    RoleUserDTO toRoleUserDTO(User user);
}
