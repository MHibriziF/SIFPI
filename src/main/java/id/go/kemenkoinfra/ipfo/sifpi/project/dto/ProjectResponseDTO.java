package id.go.kemenkoinfra.ipfo.sifpi.project.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectResponseDTO {

    private Long id;
    private UUID ownerId;
    private String name;
    private String description;
    private String sector;
    private String status;
    private String location;
    private String valueProposition;
    private String locationImageUrl;
    private String ownerInstitution;
    private String contactPersonName;
    private String contactPersonEmail;
    private String contactPersonPhone;
    private String cooperationModel;
    private Integer concessionPeriod;
    private String assetReadiness;
    private String projectStructureImageUrl;
    private String governmentSupport;
    private BigDecimal totalCapex;
    private BigDecimal totalOpex;
    private BigDecimal npv;
    private BigDecimal irr;
    private String revenueStream;
    private String projectFileDownloadUrl;
    private Boolean isFeasibilityStudy;
    private String additionalInfo;
    private List<ProjectTimelineResponseDTO> timelines;
    private Boolean isSubmitted;
}
