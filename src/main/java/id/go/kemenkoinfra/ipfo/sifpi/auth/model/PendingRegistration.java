package id.go.kemenkoinfra.ipfo.sifpi.auth.model;

import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Temporary registration record - holds user data until email verification is completed.
 * After successful email verification, data is transferred to User entity.
 * This record is then deleted.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(
    name = "pending_registrations",
    indexes = {
        @Index(name = "idx_pending_email", columnList = "email"),
        @Index(name = "idx_pending_token", columnList = "email_verification_token"),
        @Index(name = "idx_pending_created_at", columnList = "created_at")
    }
)
public class PendingRegistration {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true, length = 255)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, length = 255)
    private String name;

    @Column(length = 20)
    private String phone;

    @Column(length = 255)
    private String organization;

    @Column(length = 100)
    private String jabatan;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

    // Email verification token
    @Column(nullable = false, length = 100)
    private String emailVerificationToken;

    @Column(nullable = false)
    private LocalDateTime emailVerificationTokenExpiry;

    // Additional data for investor profile (stored as JSON)
    @Column(columnDefinition = "json")
    private String investorProfileData; // JSON: {budgetInvestasi, sectorInterest: [...]}

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
