package id.go.kemenkoinfra.ipfo.sifpi.auth.mapper;

import id.go.kemenkoinfra.ipfo.sifpi.auth.dto.AuthResponseDTO;
import id.go.kemenkoinfra.ipfo.sifpi.auth.model.RolePermission;
import id.go.kemenkoinfra.ipfo.sifpi.auth.model.User;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-03-07T20:22:31+0700",
    comments = "version: 1.6.3, compiler: IncrementalProcessingEnvironment from gradle-language-java-9.3.0.jar, environment: Java 21.0.8 (Eclipse Adoptium)"
)
@Component
public class AuthMapperImpl implements AuthMapper {

    @Autowired
    private PermissionsMapper permissionsMapper;

    @Override
    public AuthResponseDTO toDTO(User user, List<RolePermission> permissions) {
        if ( user == null && permissions == null ) {
            return null;
        }

        AuthResponseDTO authResponseDTO = new AuthResponseDTO();

        if ( user != null ) {
            authResponseDTO.setId( user.getId() );
            authResponseDTO.setEmail( user.getEmail() );
            authResponseDTO.setName( user.getName() );
        }
        authResponseDTO.setPermissions( permissionsMapper.toPermissionsDTO( permissions ) );
        authResponseDTO.setRole( user.getRole().getName() );

        return authResponseDTO;
    }
}
