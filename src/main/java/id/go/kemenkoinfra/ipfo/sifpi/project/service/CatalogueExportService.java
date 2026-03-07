package id.go.kemenkoinfra.ipfo.sifpi.project.service;

import id.go.kemenkoinfra.ipfo.sifpi.project.dto.CatalogueExportResponseDTO;
import id.go.kemenkoinfra.ipfo.sifpi.project.dto.request.CatalogueExportRequest;

public interface CatalogueExportService {

    CatalogueExportResponseDTO exportCatalogue(CatalogueExportRequest request);
}
