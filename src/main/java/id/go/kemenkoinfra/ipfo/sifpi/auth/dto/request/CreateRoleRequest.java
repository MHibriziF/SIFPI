package id.go.kemenkoinfra.ipfo.sifpi.auth.dto.request;

import java.util.ArrayList;
import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateRoleRequest {

    @NotBlank
    @Size(max = 50)
    private String name;

    @NotBlank
    @Size(max = 200)
    private String description;

    private boolean status = true;

    @Valid
    private List<PermissionRequest> permissions = new ArrayList<>();
}
