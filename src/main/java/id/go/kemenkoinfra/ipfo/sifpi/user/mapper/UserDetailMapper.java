package id.go.kemenkoinfra.ipfo.sifpi.user.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import id.go.kemenkoinfra.ipfo.sifpi.auth.model.Role;
import id.go.kemenkoinfra.ipfo.sifpi.auth.model.User;
import id.go.kemenkoinfra.ipfo.sifpi.user.dto.UserDetailDTO;

// NOTE: this file is an orphan left from a refactor — do NOT add componentModel = "spring"
// The active version lives in usermanagement/mapper/UserDetailMapper.java
@Mapper
public interface UserDetailMapper {

    @Mapping(source = "role", target = "role", qualifiedByName = "roleToString")
    @Mapping(source = "organization", target = "organisasi")
    @Mapping(source = "emailVerified", target = "emailVerified")
    @Mapping(target = "lastLogin", ignore = true)
    @Mapping(target = "phoneVerified", ignore = true)
    @Mapping(target = "jumlahProyek", ignore = true)
    @Mapping(target = "inquiryMasuk", ignore = true)
    @Mapping(target = "companyInfo", ignore = true)
    @Mapping(target = "sectorInterest", ignore = true)
    @Mapping(target = "projectInterest", ignore = true)
    @Mapping(target = "budgetRange", ignore = true)
    UserDetailDTO toUserDetailDTO(User user);

    @Named("roleToString")
    default String roleToString(Role role) {
        return role != null ? role.getName() : null;
    }
}
