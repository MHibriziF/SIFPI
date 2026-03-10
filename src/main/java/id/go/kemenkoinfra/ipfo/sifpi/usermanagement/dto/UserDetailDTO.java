package id.go.kemenkoinfra.ipfo.sifpi.usermanagement.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserDetailDTO {

    private UUID id;
    private String email;

    @JsonProperty("nama")
    private String name;

    private String phone;
    private String role;
    private String organisasi;
    private String jabatan;

    // ── Audit trail ─────────────────────────────────────────────────────────

    @JsonProperty("created_at")
    private LocalDateTime createdAt;

    @JsonProperty("last_login")
    private LocalDateTime lastLogin;

    @JsonProperty("email_verified")
    private Boolean emailVerified;

    /**
     * For PROJECT_OWNER only: true if admin has verified the owner's account
     * via PATCH /api/admin/users/{email}/verify.
     * Null for all other roles.
     */
    @JsonProperty("owner_verified")
    private Boolean ownerVerified;

    @JsonProperty("is_active")
    private Boolean isActive;

    /** Not yet tracked in the model — always null until a phoneVerified field is added to User. */
    @JsonProperty("phone_verified")
    private Boolean phoneVerified;

    // ── Project Owner specific ───────────────────────────────────────────────

    @JsonProperty("jumlah_proyek")
    private Integer jumlahProyek;

    @JsonProperty("inquiry_masuk")
    private Integer inquiryMasuk;

    // ── Investor specific ────────────────────────────────────────────────────

    @JsonProperty("company_info")
    private CompanyInfoDTO companyInfo;

    @JsonProperty("sector_interest")
    private List<String> sectorInterest;

    /** Placeholder — no ProjectInterest model exists yet. */
    @JsonProperty("project_interest")
    private Object projectInterest;

    @JsonProperty("budget_range")
    private String budgetRange;

    // ── Nested DTOs ──────────────────────────────────────────────────────────

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CompanyInfoDTO {
        private String name;
        private String sector;

        /** Not yet tracked in the model — always null until an industryType field is added. */
        @JsonProperty("industry_type")
        private String industryType;
    }
}
