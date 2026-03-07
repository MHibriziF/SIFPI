package id.go.kemenkoinfra.ipfo.sifpi.project.dto;

import id.go.kemenkoinfra.ipfo.sifpi.common.enums.ProjectStatus;
import id.go.kemenkoinfra.ipfo.sifpi.common.enums.Sector;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO for admin project list view with owner information
 * PM-9: Read All Projects (Admin)
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminProjectListItemDTO {
    private Long id;
    private String name;
    private Sector sector;
    private ProjectStatus status;
    private UUID ownerId;
    private String ownerName;
    private String ownerOrganization;
    private BigDecimal totalCapex;
    private BigDecimal totalOpex;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
