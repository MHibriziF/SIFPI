package id.go.kemenkoinfra.ipfo.sifpi.usermanagement.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserStatusRequest {
    @NotNull(message = "isActive tidak boleh kosong")
    private Boolean isActive;
}
