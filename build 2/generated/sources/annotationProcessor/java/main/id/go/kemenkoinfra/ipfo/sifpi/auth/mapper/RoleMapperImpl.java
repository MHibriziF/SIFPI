package id.go.kemenkoinfra.ipfo.sifpi.auth.mapper;

import id.go.kemenkoinfra.ipfo.sifpi.auth.dto.RoleResponseDTO;
import id.go.kemenkoinfra.ipfo.sifpi.auth.dto.request.CreateRoleRequest;
import id.go.kemenkoinfra.ipfo.sifpi.auth.model.Role;
import id.go.kemenkoinfra.ipfo.sifpi.auth.model.RolePermission;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-03-07T20:22:32+0700",
    comments = "version: 1.6.3, compiler: IncrementalProcessingEnvironment from gradle-language-java-9.3.0.jar, environment: Java 21.0.8 (Eclipse Adoptium)"
)
@Component
public class RoleMapperImpl implements RoleMapper {

    @Autowired
    private PermissionsMapper permissionsMapper;

    @Override
    public Role toEntity(CreateRoleRequest request) {
        if ( request == null ) {
            return null;
        }

        Role role = new Role();

        role.setName( request.getName() );
        role.setDescription( request.getDescription() );
        role.setStatus( request.isStatus() );

        return role;
    }

    @Override
    public RoleResponseDTO toDTO(Role role, List<RolePermission> permissions) {
        if ( role == null && permissions == null ) {
            return null;
        }

        RoleResponseDTO roleResponseDTO = new RoleResponseDTO();

        if ( role != null ) {
            roleResponseDTO.setId( role.getId() );
            roleResponseDTO.setName( role.getName() );
            roleResponseDTO.setDescription( role.getDescription() );
            roleResponseDTO.setStatus( role.isStatus() );
        }
        roleResponseDTO.setPermissions( permissionsMapper.toPermissionsDTO( permissions ) );

        return roleResponseDTO;
    }
}
