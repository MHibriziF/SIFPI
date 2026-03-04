package id.go.kemenkoinfra.ipfo.sifpi.project.dto.request;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateProjectRequest {

    @NotBlank(message = "Nama proyek wajib diisi")
    @Size(max = 200, message = "Nama proyek maksimal 200 karakter")
    private String name;

    @NotBlank(message = "Deskripsi singkat wajib diisi")
    @Size(max = 300, message = "Deskripsi singkat maksimal 300 karakter")
    private String description;

    @NotBlank(message = "Sektor proyek wajib diisi")
    private String sector;

    private String status = "DRAFT";

    @NotBlank(message = "Lokasi proyek wajib diisi")
    private String location;

    @NotBlank(message = "Value Proposition wajib diisi")
    private String valueProposition;

    @NotBlank(message = "Instansi pemilik wajib diisi")
    private String ownerInstitution;

    @NotBlank(message = "Nama narahubung wajib diisi")
    private String contactPersonName;

    @NotBlank(message = "Email narahubung wajib diisi")
    @Email(message = "Format email tidak valid")
    private String contactPersonEmail;

    @NotBlank(message = "Nomor telepon narahubung wajib diisi")
    @Pattern(regexp = "^[0-9+\\- ]+$", message = "format telepon tidak valid")
    private String contactPersonPhone;

    @NotBlank(message = "Model kerja sama wajib diisi")
    private String cooperationModel;

    @NotNull(message = "Masa konsesi wajib diisi")
    @Min(value = 1, message = "Masa konsesi minimal 1 tahun")
    private Integer concessionPeriod;

    @NotBlank(message = "Kesiapan aset wajib diisi")
    private String assetReadiness;

    @NotBlank(message = "Bantuan pemerintah wajib diisi")
    private String governmentSupport;

    @NotNull(message = "Total capex wajib diisi")
    @DecimalMin(value = "0.0", message = "Total capex minimal 0")
    private BigDecimal totalCapex;

    @NotNull(message = "Total opex wajib diisi")
    @DecimalMin(value = "0.0", message = "Total opex minimal 0")
    private BigDecimal totalOpex;

    @NotNull(message = "NPV wajib diisi")
    @DecimalMin(value = "0.0", message = "NPV minimal 0")
    private BigDecimal npv;

    @NotNull(message = "IRR wajib diisi")
    @DecimalMin(value = "0.0", message = "IRR minimal 0")
    private BigDecimal irr;

    @NotBlank(message = "Revenue stream wajib diisi")
    private String revenueStream;

    @NotNull(message = "Status feasibility study wajib diisi")
    private Boolean isFeasibilityStudy;

    private String additionalInfo;

    @Valid
    private List<ProjectTimelineRequest> timelines = new ArrayList<>();

    @NotNull(message = "Status submit wajib diisi")
    private Boolean isSubmitted = false;
}
