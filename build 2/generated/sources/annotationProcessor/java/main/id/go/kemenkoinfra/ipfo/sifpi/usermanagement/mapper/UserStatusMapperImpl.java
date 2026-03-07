package id.go.kemenkoinfra.ipfo.sifpi.usermanagement.mapper;

import id.go.kemenkoinfra.ipfo.sifpi.auth.model.Role;
import id.go.kemenkoinfra.ipfo.sifpi.auth.model.User;
import id.go.kemenkoinfra.ipfo.sifpi.usermanagement.dto.UserStatusResponseDTO;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-03-07T20:22:34+0700",
    comments = "version: 1.6.3, compiler: IncrementalProcessingEnvironment from gradle-language-java-9.3.0.jar, environment: Java 21.0.8 (Eclipse Adoptium)"
)
@Component
public class UserStatusMapperImpl implements UserStatusMapper {

    @Override
    public UserStatusResponseDTO toDTO(User user) {
        if ( user == null ) {
            return null;
        }

        UserStatusResponseDTO userStatusResponseDTO = new UserStatusResponseDTO();

        userStatusResponseDTO.setActive( user.isActive() );
        userStatusResponseDTO.setChangedBy( user.getChangedBy() );
        userStatusResponseDTO.setChangedAt( user.getChangedAt() );
        userStatusResponseDTO.setAction( user.getAction() );
        userStatusResponseDTO.setRoleName( userRoleName( user ) );
        userStatusResponseDTO.setId( user.getId() );
        userStatusResponseDTO.setEmail( user.getEmail() );
        userStatusResponseDTO.setName( user.getName() );
        userStatusResponseDTO.setOrganization( user.getOrganization() );

        return userStatusResponseDTO;
    }

    private String userRoleName(User user) {
        Role role = user.getRole();
        if ( role == null ) {
            return null;
        }
        return role.getName();
    }
}
