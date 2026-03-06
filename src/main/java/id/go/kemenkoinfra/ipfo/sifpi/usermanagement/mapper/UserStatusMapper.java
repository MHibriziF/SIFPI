package id.go.kemenkoinfra.ipfo.sifpi.usermanagement.mapper;

import id.go.kemenkoinfra.ipfo.sifpi.auth.model.User;
import id.go.kemenkoinfra.ipfo.sifpi.usermanagement.dto.UserStatusResponseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserStatusMapper {
    
    @Mapping(source = "active", target = "active")
    @Mapping(source = "changedBy", target = "changedBy")
    @Mapping(source = "changedAt", target = "changedAt")
    @Mapping(source = "action", target = "action")
    @Mapping(source = "role.name", target = "roleName")
    UserStatusResponseDTO toDTO(User user);
}
