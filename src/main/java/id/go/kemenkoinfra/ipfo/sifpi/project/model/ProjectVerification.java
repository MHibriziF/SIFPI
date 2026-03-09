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
 * Entity for tracking project verification/approval records
 * Records admin actions: VERIFIED (approve) or REJECTED (reject + request revision)
 */
@Entity
@Table(
    name = "project_verifications",
    indexes = {
        @Index(name = "idx_pv_project_verifiedat", columnList = "project_id, verified_at")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProjectVerification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Project ID wajib diisi")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @NotBlank(message = "Action wajib diisi")
    @Column(nullable = false, length = 50)
    private String action;  // VERIFIED, REJECTED

    @Column(columnDefinition = "TEXT")
    private String notes;   // Catatan dari admin

    @NotNull(message = "Verified by wajib diisi")
    @Column(nullable = false, columnDefinition = "UUID")
    private UUID verifiedBy;  // Admin ID

    @NotBlank(message = "Verified by name wajib diisi")
    @Column(nullable = false, length = 255)
    private String verifiedByName;  // Admin name

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime verifiedAt;
}
