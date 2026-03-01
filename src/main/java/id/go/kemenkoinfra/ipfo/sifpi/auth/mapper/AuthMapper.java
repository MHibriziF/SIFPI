package id.go.kemenkoinfra.ipfo.sifpi.auth.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import id.go.kemenkoinfra.ipfo.sifpi.auth.dto.AuthResponseDTO;
import id.go.kemenkoinfra.ipfo.sifpi.auth.model.RolePermission;
import id.go.kemenkoinfra.ipfo.sifpi.auth.model.User;

@Mapper(componentModel = "spring", uses = {PermissionsMapper.class})
public interface AuthMapper {

    @Mapping(target = "role", expression = "java(user.getRole().getName())")
    @Mapping(source = "permissions", target = "permissions")
    AuthResponseDTO toDTO(User user, List<RolePermission> permissions);
}
