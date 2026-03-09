package id.go.kemenkoinfra.ipfo.sifpi.project.model;

import id.go.kemenkoinfra.ipfo.sifpi.common.enums.Sector;
import id.go.kemenkoinfra.ipfo.sifpi.common.enums.ProjectStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "projects")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private UUID ownerId;

    @NotBlank(message = "Nama proyek wajib diisi")
    @Size(max = 200)
    private String name;

    @NotBlank(message = "Deskripsi singkat wajib diisi")
    @Size(max = 300)
    private String description;

    @NotNull(message = "Sektor proyek wajib diisi")
    @Enumerated(EnumType.STRING)
    private Sector sector;

    @NotNull(message = "Status proyek wajib diisi")
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private ProjectStatus status = ProjectStatus.DRAFT;

    @NotBlank(message = "Lokasi proyek wajib diisi")
    private String location;

    @NotBlank(message = "Value Proposition wajib diisi")
    @Column(columnDefinition = "TEXT")
    private String valueProposition;

    @NotBlank(message = "Gambar lokasi proyek wajib diisi")
    private String locationImageKey;

    @NotBlank(message = "Instansi pemilik wajib diisi")
    private String ownerInstitution;

    @NotBlank(message = "Nama narahubung wajib diisi")
    private String contactPersonName;

    @Email
    @NotBlank(message = "Email narahubung wajib diisi")
    private String contactPersonEmail;

    @NotBlank(message = "Nomor telepon narahubung wajib diisi")
    @Pattern(regexp = "^[0-9+\\- ]+$", message = "format telepon tidak valid")
    private String contactPersonPhone;

    @NotBlank(message = "Model kerja sama wajib diisi")
    private String cooperationModel;

    @NotNull(message = "Masa konsesi wajib diisi")
    @Min(value = 1, message = "Masa konsesi minimal 1 tahun")
    private Integer concessionPeriod;

    @NotBlank(message = "Kesiapan aset wajib diisi")
    private String assetReadiness;

    @NotBlank(message = "Gambar struktur proyek wajib diisi")
    private String projectStructureImageKey;

    @Column(columnDefinition = "TEXT")
    @NotBlank(message = "Bantuan pemerintah wajib diisi")
    private String governmentSupport;

    @DecimalMin(value = "0.0")
    private BigDecimal totalCapex;

    @DecimalMin(value = "0.0")
    private BigDecimal totalOpex;

    @DecimalMin(value = "0.0")
    private BigDecimal npv;

    @DecimalMin(value = "0.0")
    private BigDecimal irr;

    @NotBlank(message = "Revenue stream wajib diisi")
    private String revenueStream;

    @NotBlank(message = "Dokumen proyek wajib diisi")
    private String projectFileKey;

    @NotNull
    private Boolean isFeasibilityStudy;

    @Column(columnDefinition = "TEXT")
    private String additionalInfo;

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProjectTimeline> timelines;

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProjectRevision> revisions;

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProjectStatusHistory> statusHistories;

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProjectVerification> verifications;

    @Column
    @NotNull
    private Boolean isSubmitted;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime editedAt;

    @PrePersist
    private void prePersistDefaults() {
        if (status == null) {
            status = ProjectStatus.DRAFT;
        }
    }
}
