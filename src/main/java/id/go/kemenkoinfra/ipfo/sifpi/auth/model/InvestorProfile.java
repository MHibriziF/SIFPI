package id.go.kemenkoinfra.ipfo.sifpi.auth.model;

import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(exclude = "user")
@Entity
@Table(name = "investor_profiles")
public class InvestorProfile {

    @EqualsAndHashCode.Include
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    // Basic investor preferences
    @Column(length = 50)
    private String budgetRange;

    @Column(length = 1000)
    private String sectorInterest; // Stored as comma-separated values

    @Column(nullable = false)
    private Boolean optInEmail = false;

    @Column(nullable = false)
    private Boolean agreePrivacy = false;

    // Additional investor catalogue fields (UM-5 extended requirements)
    @Column(length = 100)
    private String preferredInvestmentInstrument; // e.g., "Equity", "Debt", "Grant"

    @Column(length = 100)
    private String engagementModel; // e.g., "Direct Investment", "Co-investment", "Fund"

    @Column(length = 50)
    private String stagePreference; // "Greenfield", "Brownfield", or "Both"

    @Column(length = 50)
    private String riskAppetite; // e.g., "Low", "Medium", "High"

    @Column(length = 255)
    private String esgStandards; // ESG compliance standards

    @Column(length = 255)
    private String localPresence; // Geographic presence/office locations

    @Column(length = 100)
    private String aumSize; // Assets Under Management size range

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;
}
