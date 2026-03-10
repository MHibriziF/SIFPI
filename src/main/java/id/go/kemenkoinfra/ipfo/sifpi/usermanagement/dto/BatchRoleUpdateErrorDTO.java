package id.go.kemenkoinfra.ipfo.sifpi.usermanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BatchRoleUpdateErrorDTO {

    private String email;
    private String reason;
}
