package id.go.kemenkoinfra.ipfo.sifpi.auth.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class VerificationResponseDTO {
    private UUID id;
    private String email;
    private String name;
    private String organization;
    private Boolean isVerified;
    private String verifiedBy;
    private LocalDateTime verifiedAt;
    private String roleName;
}
