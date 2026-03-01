package id.go.kemenkoinfra.ipfo.sifpi.usermanagement.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * Request DTO for updating password (UM-9).
 */
@Data
public class UpdatePasswordRequest {

    @NotBlank(message = "Password saat ini tidak boleh kosong")
    private String currentPassword;

    @NotBlank(message = "Password baru tidak boleh kosong")
    @Size(min = 8, message = "Password baru minimal 8 karakter")
    private String newPassword;
}
