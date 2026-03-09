package id.go.kemenkoinfra.ipfo.sifpi.project.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO representing a single status transition in the project's history.
 * Used by PM-4: GET /api/projects/{id}/history
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectStatusHistoryDTO {

    /** The project status at this point in time (e.g. DIAJUKAN, IN_REVIEW, TERVERIFIKASI) */
    private String status;

    /** When the status change occurred */
    private LocalDateTime changedAt;

    /** Display name of the user who made the change */
    private String changedBy;

    /** Optional notes attached to this transition (e.g. rejection reason, admin remarks) */
    private String notes;
}
