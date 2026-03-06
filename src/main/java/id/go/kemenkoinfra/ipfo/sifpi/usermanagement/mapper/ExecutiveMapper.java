package id.go.kemenkoinfra.ipfo.sifpi.usermanagement.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import id.go.kemenkoinfra.ipfo.sifpi.auth.model.User;
import id.go.kemenkoinfra.ipfo.sifpi.usermanagement.dto.CreateExecutiveRequest;
import id.go.kemenkoinfra.ipfo.sifpi.usermanagement.dto.ExecutiveDTO;

@Mapper(componentModel = "spring")
public interface ExecutiveMapper {

    @Mapping(target = "roleName", source = "role.name")
    @Mapping(target = "isVerified", source = "emailVerified")
    @Mapping(target = "isActive", source = "active")
    ExecutiveDTO toDTO(User user);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "name", source = "nama")
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "emailVerified", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "organization", ignore = true)
    @Mapping(target = "investorProfile", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    User toEntity(CreateExecutiveRequest request);
}
