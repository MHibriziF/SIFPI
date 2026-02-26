package id.go.kemenkoinfra.ipfo.sifpi.auth.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import id.go.kemenkoinfra.ipfo.sifpi.auth.dto.AuthResponseDTO;
import id.go.kemenkoinfra.ipfo.sifpi.auth.model.User;

@Mapper(componentModel = "spring")
public interface AuthMapper {

    @Mapping(target = "role", expression = "java(user.getRole().getName())")
    @Mapping(target = "permissions", ignore = true)
    AuthResponseDTO toDTO(User user);
}
