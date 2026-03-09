package id.go.kemenkoinfra.ipfo.sifpi.project.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO for project verification records
 * Represents verification actions by admin: VERIFIED (approve) or REJECTED (reject + request revision)
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectVerificationDTO {
    private Long id;
    private String action;           // VERIFIED or REJECTED
    private String notes;            // Catatan/alasan dari admin
    private UUID verifiedBy;         // Admin ID
    private String verifiedByName;   // Admin name
    private LocalDateTime verifiedAt;
}
