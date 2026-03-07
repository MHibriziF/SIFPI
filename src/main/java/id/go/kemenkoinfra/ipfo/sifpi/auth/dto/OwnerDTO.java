package id.go.kemenkoinfra.ipfo.sifpi.auth.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;


@Data
public class OwnerDTO {
    private UUID id;
    private String nama;
    private String email;
    private String phone;
    private String organisasi;
    private String jabatan;
    private Boolean isVerified;
    private Boolean isActive;
    private String roleName;
    private LocalDateTime createdAt;
}
