package id.go.kemenkoinfra.ipfo.sifpi.project.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO for bulk operations on projects (publish, unpublish, etc)
 * Generic request containing list of project IDs
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BulkPublishRequest {
    
    @NotNull(message = "Project IDs wajib diisi")
    @NotEmpty(message = "Project IDs tidak boleh kosong")
    private List<Long> projectIds;
}
