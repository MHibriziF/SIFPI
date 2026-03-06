package id.go.kemenkoinfra.ipfo.sifpi.auth.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import id.go.kemenkoinfra.ipfo.sifpi.auth.dto.VerificationResponseDTO;
import id.go.kemenkoinfra.ipfo.sifpi.auth.model.User;

@Mapper(componentModel = "spring")
public interface VerificationMapper {

    @Mapping(target = "roleName", expression = "java(user.getRole().getName())")
    VerificationResponseDTO toVerificationDTO(User user);
}
