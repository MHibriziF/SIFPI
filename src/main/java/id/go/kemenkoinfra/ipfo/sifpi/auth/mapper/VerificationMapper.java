package id.go.kemenkoinfra.ipfo.sifpi.auth.mapper;

import org.mapstruct.Mapper;

import id.go.kemenkoinfra.ipfo.sifpi.auth.dto.VerificationResponseDTO;
import id.go.kemenkoinfra.ipfo.sifpi.auth.model.ProjectOwnerProfile;
import id.go.kemenkoinfra.ipfo.sifpi.auth.model.User;

@Mapper(componentModel = "spring")
public interface VerificationMapper {

    // Map User + ProjectOwnerProfile to VerificationResponseDTO
    default VerificationResponseDTO toVerificationDTO(User user, ProjectOwnerProfile poProfile) {
        if (user == null || poProfile == null) {
            return null;
        }
        
        VerificationResponseDTO dto = new VerificationResponseDTO();
        dto.setId(user.getId());
        dto.setEmail(user.getEmail());
        dto.setName(user.getName());
        dto.setOrganization(user.getOrganization());
        dto.setIsVerified(poProfile.isVerified());
        dto.setVerifiedBy(poProfile.getVerifiedBy());
        dto.setVerifiedAt(poProfile.getVerifiedAt());
        dto.setRoleName(user.getRole().getName());
        
        return dto;
    }
}
