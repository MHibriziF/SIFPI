package id.go.kemenkoinfra.ipfo.sifpi.project.dto.request;

import java.math.BigDecimal;
import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EditProjectRequest {

    @Size(max = 200, message = "Nama proyek maksimal 200 karakter")
    private String name;

    @Size(max = 300, message = "Deskripsi singkat maksimal 300 karakter")
    private String description;

    private String sector;
    private String status;
    private String location;
    private String valueProposition;
    private String locationImageKey;
    private String ownerInstitution;
    private String contactPersonName;

    @Email(message = "Format email tidak valid")
    private String contactPersonEmail;

    @Pattern(regexp = "^[0-9+\\- ]+$", message = "format telepon tidak valid")
    private String contactPersonPhone;

    private String cooperationModel;

    @Min(value = 1, message = "Masa konsesi minimal 1 tahun")
    private Integer concessionPeriod;

    private String assetReadiness;
    private String projectStructureImageKey;
    private String governmentSupport;

    @DecimalMin(value = "0.0", message = "Total capex minimal 0")
    private BigDecimal totalCapex;

    @DecimalMin(value = "0.0", message = "Total opex minimal 0")
    private BigDecimal totalOpex;

    @DecimalMin(value = "0.0", message = "NPV minimal 0")
    private BigDecimal npv;

    @DecimalMin(value = "0.0", message = "IRR minimal 0")
    private BigDecimal irr;

    private String revenueStream;
    private String projectFileKey;
    private Boolean isFeasibilityStudy;
    private String additionalInfo;

    @Valid
    private List<ProjectTimelineRequest> timelines;

    private Boolean isSubmitted;
}
