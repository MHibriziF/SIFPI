package id.go.kemenkoinfra.ipfo.sifpi.project.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BulkInsertProjectRowErrorDTO {

    private int row;
    private List<String> reasons;
}
