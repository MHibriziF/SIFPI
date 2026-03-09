package id.go.kemenkoinfra.ipfo.sifpi.auth.service;

import id.go.kemenkoinfra.ipfo.sifpi.auth.dto.OrganizationDTO;

import java.util.List;

public interface OrganizationService {

    /**
     * Get all organizations sorted by name
     */
    List<OrganizationDTO> getAllOrganizations();

    /**
     * Search organizations by name (for autocomplete)
     */
    List<OrganizationDTO> searchOrganizations(String searchTerm);

    /**
     * Create new organization if not exists, otherwise return existing
     */
    OrganizationDTO getOrCreateOrganization(String name);
}
