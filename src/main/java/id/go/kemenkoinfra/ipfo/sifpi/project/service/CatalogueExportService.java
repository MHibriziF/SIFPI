package id.go.kemenkoinfra.ipfo.sifpi.project.service;

import id.go.kemenkoinfra.ipfo.sifpi.project.dto.CatalogueExportFileDTO;
import id.go.kemenkoinfra.ipfo.sifpi.project.dto.request.CatalogueExportRequest;

public interface CatalogueExportService {

    CatalogueExportFileDTO exportCatalogue(CatalogueExportRequest request);
}
