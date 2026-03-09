package id.go.kemenkoinfra.ipfo.sifpi.project.dto.request;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class BulkInsertProjectRequest {

    private String ownerEmail;
    private String name;
    private String description;
    private String sector;
    private String location;
    private String valueProposition;
    private String ownerInstitution;
    private String contactPersonName;
    private String contactPersonEmail;
    private String contactPersonPhone;
    private String cooperationModel;
    private Integer concessionPeriod;
    private String assetReadiness;
    private String governmentSupport;
    private BigDecimal totalCapex;
    private BigDecimal totalOpex;
    private BigDecimal npv;
    private BigDecimal irr;
    private String revenueStream;

    @JsonProperty("is_feasibility_study")
    private Boolean isFeasibilityStudy;

    private String additionalInfo;

    private String locationImageUrl;
    private String projectStructureImageUrl;
    private String projectFileUrl;
}
