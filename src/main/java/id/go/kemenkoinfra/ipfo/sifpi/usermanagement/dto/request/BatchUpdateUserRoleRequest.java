package id.go.kemenkoinfra.ipfo.sifpi.usermanagement.dto.request;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BatchUpdateUserRoleRequest {

    @NotEmpty(message = "Daftar pembaruan role tidak boleh kosong")
    @Valid
    private List<UserRoleUpdateItem> updates;
}
