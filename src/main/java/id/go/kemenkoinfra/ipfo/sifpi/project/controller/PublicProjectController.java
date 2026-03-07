package id.go.kemenkoinfra.ipfo.sifpi.project.controller;

import id.go.kemenkoinfra.ipfo.sifpi.common.dto.BaseResponseDTO;
import id.go.kemenkoinfra.ipfo.sifpi.common.dto.PagedResponseDTO;
import id.go.kemenkoinfra.ipfo.sifpi.common.enums.Sector;
import id.go.kemenkoinfra.ipfo.sifpi.common.utils.ResponseUtil;
import id.go.kemenkoinfra.ipfo.sifpi.project.dto.PublicProjectCatalogueDTO;
import id.go.kemenkoinfra.ipfo.sifpi.project.service.PublicProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
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
     * @param pageable Pagination parameters (page, size, sort)
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
            @PageableDefault(size = 12, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

        Page<PublicProjectCatalogueDTO> result = publicProjectService.getPublishedProjects(
                sector,
                location,
                cooperationModel,
                minBudget,
                maxBudget,
                search,
                pageable
        );

        // Convert Page to PagedResponseDTO
        PagedResponseDTO<PublicProjectCatalogueDTO> pagedResponse = PagedResponseDTO.<PublicProjectCatalogueDTO>builder()
                .content(result.getContent())
                .page(result.getNumber())
                .size(result.getSize())
                .totalElements(result.getTotalElements())
                .totalPages(result.getTotalPages())
                .first(result.isFirst())
                .last(result.isLast())
                .build();

        return responseUtil.success(pagedResponse, "Katalog proyek berhasil diambil.", HttpStatus.OK);
    }
}
