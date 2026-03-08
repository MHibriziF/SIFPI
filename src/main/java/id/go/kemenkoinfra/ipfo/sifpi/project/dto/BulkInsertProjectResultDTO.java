package id.go.kemenkoinfra.ipfo.sifpi.project.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BulkInsertProjectResultDTO {

    private int successCount;
    private int failedCount;
    private List<BulkInsertProjectRowErrorDTO> errors;
}
