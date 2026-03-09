package id.go.kemenkoinfra.ipfo.sifpi.auth.dto;

import java.util.UUID;

import lombok.Data;

@Data
public class RoleSummaryDTO {

    private UUID id;
    private String name;
    private String description;
    private long userCount;
}
