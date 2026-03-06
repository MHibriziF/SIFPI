package id.go.kemenkoinfra.ipfo.sifpi.usermanagement.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BulkInsertRowErrorDTO {
    private int row;
    private List<String> reasons;
}
