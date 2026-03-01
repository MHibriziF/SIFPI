package id.go.kemenkoinfra.ipfo.sifpi.auth.mapper;

import java.util.List;
import java.util.stream.Collectors;

import org.mapstruct.Mapper;

import id.go.kemenkoinfra.ipfo.sifpi.auth.dto.PermissionsDTO;
import id.go.kemenkoinfra.ipfo.sifpi.auth.model.RolePermission;

@Mapper(componentModel = "spring")
public interface PermissionsMapper {

    default PermissionsDTO toPermissionsDTO(List<RolePermission> permissions) {
        PermissionsDTO dto = new PermissionsDTO();
        dto.putAll(permissions.stream()
                .collect(Collectors.groupingBy(RolePermission::getResourceName,
                        Collectors.mapping(RolePermission::getActionName, Collectors.toList()))));
        return dto;
    }
}
