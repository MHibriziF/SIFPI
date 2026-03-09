package id.go.kemenkoinfra.ipfo.sifpi.project.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entity for tracking project status history/transitions
 * Records when and how project status changes (DRAFT -> IN_REVIEW -> TERVERIFIKASI, etc)
 */
@Entity
@Table(
    name = "project_status_histories",
    indexes = {
        @Index(name = "idx_psh_project_changedat", columnList = "project_id, changed_at")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProjectStatusHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Project ID wajib diisi")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @NotBlank(message = "Status wajib diisi")
    @Column(nullable = false, length = 50)
    private String status;  // Current status yang dicapai (IN_REVIEW, TERVERIFIKASI, etc)

    @NotNull(message = "Changed by wajib diisi")
    @Column(nullable = false, columnDefinition = "UUID")
    private UUID changedBy;  // Who changed the status (Owner or Admin ID)

    @NotBlank(message = "Changed by name wajib diisi")
    @Column(nullable = false, length = 255)
    private String changedByName;  // Name of person who changed status

    @Column(columnDefinition = "TEXT")
    private String notes;   // Notes/reason for status change

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime changedAt;
}
