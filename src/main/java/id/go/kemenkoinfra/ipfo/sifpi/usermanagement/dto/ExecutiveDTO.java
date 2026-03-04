package id.go.kemenkoinfra.ipfo.sifpi.usermanagement.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExecutiveDTO {

    private UUID id;
    private String email;
    private String name;
    private String phone;
    private String jabatan;
    private String roleName;
    private boolean isVerified;
    private boolean emailVerified;
    private boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
