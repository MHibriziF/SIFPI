package id.go.kemenkoinfra.ipfo.sifpi.usermanagement.dto.request;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class UpdateProfileRequest {

    // ── Common fields (all roles) ────────────────────────────────────────────

    @NotBlank(message = "Nama tidak boleh kosong")
    private String name;

    @NotBlank(message = "Email tidak boleh kosong")
    @Email(message = "Format email tidak valid")
    private String email;

    @NotBlank(message = "Nomor telepon tidak boleh kosong")
    @Pattern(regexp = "^[+]?[0-9][0-9\\s\\-]{6,18}[0-9]$", message = "Format nomor telepon tidak valid")
    @JsonProperty("phone_number")
    private String phoneNumber;

    // ── Project Owner specific (optional) ────────────────────────────────────

    @JsonProperty("institution_name")
    private String institutionName;

    private String position;

    // ── Investor specific (optional) ─────────────────────────────────────────

    @JsonProperty("company_name")
    private String companyName;

    @JsonProperty("investment_interest_sectors")
    private List<String> investmentInterestSectors;

    @JsonProperty("investment_scale")
    private String investmentScale;
}
