package id.go.kemenkoinfra.ipfo.sifpi.auth.dto.request;

import java.util.ArrayList;
import java.util.List;

import id.go.kemenkoinfra.ipfo.sifpi.common.enums.Action;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PermissionRequest {

    @NotBlank
    private String resource;

    @NotEmpty
    private List<@NotNull Action> actions = new ArrayList<>();
}
