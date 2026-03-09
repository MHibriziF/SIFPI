package id.go.kemenkoinfra.ipfo.sifpi.auth.dto.request;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateRoleRequest {

    @Size(max = 50)
    private String name;

    @Size(max = 200)
    private String description;

    private Boolean status;

    @Valid
    private List<PermissionRequest> permissions;
}
