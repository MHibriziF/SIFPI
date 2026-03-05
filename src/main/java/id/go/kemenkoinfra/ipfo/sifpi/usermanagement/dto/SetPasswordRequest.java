package id.go.kemenkoinfra.ipfo.sifpi.usermanagement.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * Request DTO for setting password (first time) via token from email.
 */
@Data
public class SetPasswordRequest {

    @NotBlank(message = "Token tidak boleh kosong")
    private String token;

    @NotBlank(message = "Password tidak boleh kosong")
    @Size(min = 8, message = "Password minimal 8 karakter")
    private String password;

    @NotBlank(message = "Konfirmasi password tidak boleh kosong")
    private String confirmPassword;
}
