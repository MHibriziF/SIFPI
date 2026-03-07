package id.go.kemenkoinfra.ipfo.sifpi.usermanagement.mapper;

import id.go.kemenkoinfra.ipfo.sifpi.auth.model.Role;
import id.go.kemenkoinfra.ipfo.sifpi.auth.model.User;
import id.go.kemenkoinfra.ipfo.sifpi.usermanagement.dto.CreateExecutiveRequest;
import id.go.kemenkoinfra.ipfo.sifpi.usermanagement.dto.ExecutiveDTO;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-03-07T20:22:34+0700",
    comments = "version: 1.6.3, compiler: IncrementalProcessingEnvironment from gradle-language-java-9.3.0.jar, environment: Java 21.0.8 (Eclipse Adoptium)"
)
@Component
public class ExecutiveMapperImpl implements ExecutiveMapper {

    @Override
    public ExecutiveDTO toDTO(User user) {
        if ( user == null ) {
            return null;
        }

        ExecutiveDTO.ExecutiveDTOBuilder executiveDTO = ExecutiveDTO.builder();

        executiveDTO.roleName( userRoleName( user ) );
        executiveDTO.isVerified( user.isEmailVerified() );
        executiveDTO.isActive( user.isActive() );
        executiveDTO.id( user.getId() );
        executiveDTO.email( user.getEmail() );
        executiveDTO.name( user.getName() );
        executiveDTO.phone( user.getPhone() );
        executiveDTO.jabatan( user.getJabatan() );
        executiveDTO.emailVerified( user.isEmailVerified() );
        executiveDTO.createdAt( user.getCreatedAt() );
        executiveDTO.updatedAt( user.getUpdatedAt() );

        return executiveDTO.build();
    }

    @Override
    public User toEntity(CreateExecutiveRequest request) {
        if ( request == null ) {
            return null;
        }

        User user = new User();

        user.setName( request.getNama() );
        user.setEmail( request.getEmail() );
        user.setPhone( request.getPhone() );
        user.setJabatan( request.getJabatan() );

        return user;
    }

    private String userRoleName(User user) {
        Role role = user.getRole();
        if ( role == null ) {
            return null;
        }
        return role.getName();
    }
}
