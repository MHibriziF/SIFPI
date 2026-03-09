package id.go.kemenkoinfra.ipfo.sifpi.project.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for rejecting a project
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RejectProjectRequest {
    
    @NotNull(message = "Notes wajib diisi")
    @NotEmpty(message = "Notes tidak boleh kosong")
    @Size(min = 10, max = 500, message = "Notes minimal 10 karakter dan maksimal 500 karakter")
    private String notes;
}
