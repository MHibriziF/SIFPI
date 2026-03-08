package id.go.kemenkoinfra.ipfo.sifpi.usermanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserStatusResponseDTO {
    private UUID id;
    private String email;
    private String name;
    private String organization;
    private Boolean active;
    private String changedBy;
    private LocalDateTime changedAt;
    private String action;
    private String roleName;
}
