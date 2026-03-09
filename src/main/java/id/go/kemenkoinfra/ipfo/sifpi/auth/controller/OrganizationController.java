package id.go.kemenkoinfra.ipfo.sifpi.auth.controller;

import id.go.kemenkoinfra.ipfo.sifpi.auth.dto.OrganizationDTO;
import id.go.kemenkoinfra.ipfo.sifpi.auth.service.OrganizationService;
import id.go.kemenkoinfra.ipfo.sifpi.common.dto.BaseResponseDTO;
import id.go.kemenkoinfra.ipfo.sifpi.common.utils.ResponseUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/auth/organizations")
@RequiredArgsConstructor
public class OrganizationController {

    private final OrganizationService organizationService;
    private final ResponseUtil responseUtil;

    /**
     * Get all organizations sorted by name
     * GET /api/auth/organizations
     */
    @GetMapping
    public ResponseEntity<BaseResponseDTO<List<OrganizationDTO>>> getAllOrganizations() {
        List<OrganizationDTO> organizations = organizationService.getAllOrganizations();
        return responseUtil.success(organizations, "Daftar organisasi berhasil diambil.", HttpStatus.OK);
    }

    /**
     * Search organizations by name (for autocomplete)
     * GET /api/auth/organizations/search?q=searchTerm
     */
    @GetMapping("/search")
    public ResponseEntity<BaseResponseDTO<List<OrganizationDTO>>> searchOrganizations(
            @RequestParam(name = "q", required = false) String searchTerm) {
        List<OrganizationDTO> organizations = organizationService.searchOrganizations(searchTerm);
        return responseUtil.success(organizations, "Pencarian organisasi berhasil.", HttpStatus.OK);
    }

    /**
     * Get or create organization by name
     * POST /api/auth/organizations/get-or-create
     */
    @PostMapping("/get-or-create")
    public ResponseEntity<BaseResponseDTO<OrganizationDTO>> getOrCreateOrganization(
            @RequestBody OrganizationDTO request) {
        OrganizationDTO organization = organizationService.getOrCreateOrganization(request.getName());
        return responseUtil.success(organization, "Organisasi berhasil diambil atau dibuat.", HttpStatus.OK);
    }
}
