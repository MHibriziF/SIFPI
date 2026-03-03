package id.go.kemenkoinfra.ipfo.sifpi.user.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import id.go.kemenkoinfra.ipfo.sifpi.auth.model.Role;
import id.go.kemenkoinfra.ipfo.sifpi.auth.model.User;
import id.go.kemenkoinfra.ipfo.sifpi.user.dto.UserDTO;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(source = "role", target = "role", qualifiedByName = "roleToString")
    UserDTO toUserDTO(User user);

    @Named("roleToString")
    default String roleToString(Role role) {
        return role != null ? role.getName() : null;
    }
}
