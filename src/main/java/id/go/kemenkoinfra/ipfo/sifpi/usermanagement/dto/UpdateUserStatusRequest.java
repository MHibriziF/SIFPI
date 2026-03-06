package id.go.kemenkoinfra.ipfo.sifpi.usermanagement.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserStatusRequest {
    @NotNull(message = "is_active tidak boleh kosong")
    @JsonProperty("is_active")
    private Boolean isActive;
}
