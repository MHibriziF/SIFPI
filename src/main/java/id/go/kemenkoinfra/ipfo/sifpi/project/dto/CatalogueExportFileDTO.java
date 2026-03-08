package id.go.kemenkoinfra.ipfo.sifpi.project.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CatalogueExportFileDTO {

    private String filename;
    private byte[] data;
}
