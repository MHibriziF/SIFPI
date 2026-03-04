package id.go.kemenkoinfra.ipfo.sifpi.auth.dto.request;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class CreateInvestorRequest {

    // ===== BAGIAN 1: DATA DIRI DAN ORGANISASI =====

    @NotBlank(message = "Nama lengkap wajib diisi")
    @Size(max = 255, message = "Nama lengkap maksimal 255 karakter")
    private String nama;

    @NotBlank(message = "Email resmi wajib diisi")
    @Email(message = "Format email tidak valid")
    @Size(max = 255, message = "Email maksimal 255 karakter")
    private String email;

    @NotBlank(message = "Jabatan wajib diisi")
    @Size(max = 100, message = "Jabatan maksimal 100 karakter")
    private String jabatan;

    @NotBlank(message = "No. Telepon wajib diisi")
    @Size(max = 20, message = "No. Telepon maksimal 20 karakter")
    private String phone;

    @NotBlank(message = "Password wajib diisi")
    @Size(min = 8, message = "Password minimal 8 karakter")
    private String password;

    @NotBlank(message = "Konfirmasi password wajib diisi")
    private String confirmPassword;

    @NotBlank(message = "Organisasi/Instansi wajib diisi")
    @Size(max = 255, message = "Nama organisasi maksimal 255 karakter")
    private String organisasi;

    // ===== BAGIAN 2: PREFERENSI INVESTASI & KOMUNIKASI =====

    @NotBlank(message = "Budget investasi wajib diisi")
    @Size(max = 50, message = "Budget investasi maksimal 50 karakter")
    private String budgetInvestasi;

    @NotEmpty(message = "Sektor interest wajib diisi")
    @Size(min = 3, message = "Minimal 3 sektor interest harus dipilih")
    private List<String> sectorInterest;

    @NotNull(message = "Persetujuan kebijakan privasi wajib diisi")
    @AssertTrue(message = "Anda harus menyetujui syarat dan kebijakan privasi")
    private Boolean agreePrivacy;

    private Boolean optInEmail = false;
}
