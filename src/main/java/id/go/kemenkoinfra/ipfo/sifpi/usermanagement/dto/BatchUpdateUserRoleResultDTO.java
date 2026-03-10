package id.go.kemenkoinfra.ipfo.sifpi.usermanagement.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BatchUpdateUserRoleResultDTO {

    private int totalRequested;
    private int updatedCount;
    private List<BatchRoleUpdateErrorDTO> errors;
}
