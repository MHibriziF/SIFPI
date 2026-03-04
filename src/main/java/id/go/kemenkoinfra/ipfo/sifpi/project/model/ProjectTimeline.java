package id.go.kemenkoinfra.ipfo.sifpi.project.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

@Entity
@Table(name = "project_timelines")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProjectTimeline {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Rentang waktu wajib diisi")
    private String timeRange;

    @NotBlank(message = "Deskripsi fase wajib diisi")
    @Size(max = 200)
    private String phaseDescription;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;
}