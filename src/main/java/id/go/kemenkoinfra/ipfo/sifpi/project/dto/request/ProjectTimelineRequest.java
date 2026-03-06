package id.go.kemenkoinfra.ipfo.sifpi.project.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProjectTimelineRequest {

    @NotBlank(message = "Rentang waktu wajib diisi")
    private String timeRange;

    @NotBlank(message = "Deskripsi fase wajib diisi")
    @Size(max = 200, message = "Deskripsi fase maksimal 200 karakter")
    private String phaseDescription;
}
