package id.go.kemenkoinfra.ipfo.sifpi.project.dto;

import id.go.kemenkoinfra.ipfo.sifpi.common.enums.ProjectStatus;
import id.go.kemenkoinfra.ipfo.sifpi.common.enums.Sector;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * DTO for admin project detail view with all project information
 * PM-6: Read Single Project Detail (Admin)
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminProjectDetailDTO {
    
    // Basic Project Information
    private Long id;
    private String name;
    private String description;
    private Sector sector;
    private ProjectStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Location Information
    private String location;
    private String locationImageUrl;
    
    // Value Proposition & Strategic Info
    private String valueProposition;
    
    // Project Scope Details
    private String cooperationModel;
    private Integer concessionPeriod;
    private String assetReadiness;
    private String projectStructureImageUrl;
    
    // Government Support
    private String governmentSupport;
    
    // Financial Information
    private BigDecimal totalCapex;
    private BigDecimal totalOpex;
    private BigDecimal npv;
    private BigDecimal irr;
    private String revenueStream;
    
    // Project Documents
    private String projectFileDownloadUrl;
    private Boolean isFeasibilityStudy;
    
    // Additional Information
    private String additionalInfo;
    
    // Project Owner Information
    private UUID ownerId;
    private String ownerName;
    private String ownerOrganization;
    private String ownerEmail;
    private String ownerPhone;
    
    // Contact Person Information
    private String contactPersonName;
    private String contactPersonEmail;
    private String contactPersonPhone;
    
    // Project Structure
    private String ownerInstitution;
    
    // Submission Status
    private Boolean isSubmitted;
    
    // Timeline & History
    private List<ProjectTimelineResponseDTO> timelines;
    private List<ProjectStatusHistoryDTO> statusHistory;
    private List<ProjectVerificationDTO> verifications;
}
