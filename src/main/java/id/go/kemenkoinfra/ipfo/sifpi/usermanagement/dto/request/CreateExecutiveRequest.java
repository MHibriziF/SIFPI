package id.go.kemenkoinfra.ipfo.sifpi.usermanagement.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateExecutiveRequest {

    @NotBlank(message = "Email wajib diisi")
    @Email(message = "Format email tidak valid")
    private String email;

    @NotBlank(message = "Nama wajib diisi")
    @Size(min = 2, max = 100, message = "Nama harus antara 2-100 karakter")
    private String nama;

    @NotBlank(message = "Nomor telepon wajib diisi")
    @Size(min = 10, max = 20, message = "Nomor telepon harus antara 10-20 karakter")
    private String phone;

    @NotBlank(message = "Jabatan wajib diisi")
    @Size(max = 100, message = "Jabatan maksimal 100 karakter")
    private String jabatan;
}
