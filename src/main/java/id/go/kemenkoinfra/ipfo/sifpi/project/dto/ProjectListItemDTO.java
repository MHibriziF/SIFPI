package id.go.kemenkoinfra.ipfo.sifpi.project.dto;

import id.go.kemenkoinfra.ipfo.sifpi.common.enums.ProjectStatus;
import id.go.kemenkoinfra.ipfo.sifpi.common.enums.Sector;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO for listing projects in my-projects endpoint (simplified view)
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectListItemDTO {
    private Long id;
    private String name;
    private Sector sector;
    private ProjectStatus status;
    private String location;
    private String description;
    private Boolean isSubmitted;
    private LocalDateTime createdAt;
    private LocalDateTime editedAt;
}
