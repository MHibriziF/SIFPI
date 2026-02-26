package id.go.kemenkoinfra.ipfo.sifpi.auth.dto;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import lombok.Data;

@Data
public class AuthResponseDTO {

    private UUID id;
    private String email;
    private String name;
    private String role;
    private Map<String, List<String>> permissions;
}
