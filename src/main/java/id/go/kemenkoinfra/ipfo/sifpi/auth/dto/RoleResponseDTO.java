package id.go.kemenkoinfra.ipfo.sifpi.auth.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.Data;

@Data
public class RoleResponseDTO {

    private UUID id;
    private String name;
    private String description;
    private boolean status;
    private PermissionsDTO permissions;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
