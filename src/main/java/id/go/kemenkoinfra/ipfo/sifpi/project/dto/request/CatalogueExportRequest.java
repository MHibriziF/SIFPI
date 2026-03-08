package id.go.kemenkoinfra.ipfo.sifpi.project.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CatalogueExportRequest {

    @NotEmpty(message = "projectIds wajib diisi.")
    private List<Long> projectIds;

    @NotBlank(message = "quarter wajib diisi.")
    private String quarter;

    @NotNull(message = "year wajib diisi.")
    private Integer year;
}
