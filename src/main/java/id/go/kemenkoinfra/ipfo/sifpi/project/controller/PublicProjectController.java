package id.go.kemenkoinfra.ipfo.sifpi.project.controller;

import id.go.kemenkoinfra.ipfo.sifpi.common.dto.BaseResponseDTO;
import id.go.kemenkoinfra.ipfo.sifpi.common.dto.PagedResponseDTO;
import id.go.kemenkoinfra.ipfo.sifpi.common.enums.Sector;
import id.go.kemenkoinfra.ipfo.sifpi.common.utils.ResponseUtil;
import id.go.kemenkoinfra.ipfo.sifpi.project.dto.PublicProjectCatalogueDTO;
import id.go.kemenkoinfra.ipfo.sifpi.project.service.PublicProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

/**
 * PM-10: Controller for public project catalogue
 * No authentication required - accessible by investors and public
 */
@RestController
@RequestMapping("/api/catalogue")
@RequiredArgsConstructor
public class PublicProjectController {

    private final PublicProjectService publicProjectService;
    private final ResponseUtil responseUtil;

    /**
     * PM-10: Get published projects catalogue
     * Public endpoint accessible without authentication
     *
     * @param sector Filter by project sector (optional)
     * @param location Filter by location/province (optional)
     * @param cooperationModel Filter by funding scheme/cooperation model (optional)
     * @param minBudget Minimum budget filter (optional)
     * @param maxBudget Maximum budget filter (optional)
     * @param search Search in project name and description (optional)
     * @param page Page number (0-indexed, default 0)
     * @param size Page size (default 12)
     * @param sortBy Sort field: createdAt, name, totalCapex (default createdAt)
     * @param sortDirection Sort direction: asc or desc (default desc)
     * @return Paginated list of published projects
     */
    @GetMapping
    public ResponseEntity<BaseResponseDTO<PagedResponseDTO<PublicProjectCatalogueDTO>>> getPublishedProjects(
            @RequestParam(required = false) Sector sector,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) String cooperationModel,
            @RequestParam(required = false) BigDecimal minBudget,
            @RequestParam(required = false) BigDecimal maxBudget,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDirection) {

        PagedResponseDTO<PublicProjectCatalogueDTO> result = publicProjectService.getPublishedProjects(
                sector,
                location,
                cooperationModel,
                minBudget,
                maxBudget,
                search,
                page,
                size,
                sortBy,
                sortDirection
        );

        return responseUtil.success(result, "Katalog proyek berhasil diambil.", HttpStatus.OK);
    }
}
