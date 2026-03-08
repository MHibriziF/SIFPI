package id.go.kemenkoinfra.ipfo.sifpi.project.dto;

import id.go.kemenkoinfra.ipfo.sifpi.common.enums.Sector;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * PM-10: DTO for public project catalogue
 * Used for investor and public to browse published projects
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PublicProjectCatalogueDTO {
    private Long id;
    private String name;
    private String description;
    private Sector sector;
    private String location;
    private BigDecimal totalCapex;
    private String cooperationModel;
    private String locationImageUrl;
    private LocalDateTime createdAt;
}
