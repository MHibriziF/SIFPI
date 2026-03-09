package id.go.kemenkoinfra.ipfo.sifpi.project.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO for project status history
 * Represents status transitions/changes (DRAFT -> IN_REVIEW -> TERVERIFIKASI, etc)
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectStatusHistoryDTO {
    private Long id;
    private String status;           // Status yang dicapai (IN_REVIEW, TERVERIFIKASI, etc)
    private UUID changedBy;          // Who changed the status (Owner or Admin ID)
    private String changedByName;    // Name of person who changed status
    private String notes;            // Reason/notes for status change
    private LocalDateTime changedAt; // When status changed
}
