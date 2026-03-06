package id.go.kemenkoinfra.ipfo.sifpi.auth.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class VerifyOwnerRequest {

    @NotBlank
    @Email
    private String email;
}
