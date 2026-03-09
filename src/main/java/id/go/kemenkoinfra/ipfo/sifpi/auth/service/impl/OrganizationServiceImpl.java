package id.go.kemenkoinfra.ipfo.sifpi.auth.service.impl;

import id.go.kemenkoinfra.ipfo.sifpi.auth.dto.OrganizationDTO;
import id.go.kemenkoinfra.ipfo.sifpi.auth.model.Organization;
import id.go.kemenkoinfra.ipfo.sifpi.auth.repository.OrganizationRepository;
import id.go.kemenkoinfra.ipfo.sifpi.auth.service.OrganizationService;
import id.go.kemenkoinfra.ipfo.sifpi.common.exception.ConflictException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrganizationServiceImpl implements OrganizationService {

    private final OrganizationRepository organizationRepository;

    @Override
    @Transactional(readOnly = true)
    public List<OrganizationDTO> getAllOrganizations() {
        log.info("Fetching all organizations");
        return organizationRepository.findAllByOrderByNameAsc()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrganizationDTO> searchOrganizations(String searchTerm) {
        log.info("Searching organizations with term: {}", searchTerm);
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return getAllOrganizations();
        }
        return organizationRepository.searchByNameContains(searchTerm.trim())
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public OrganizationDTO getOrCreateOrganization(String name) {
        log.info("Get or create organization: {}", name);

        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Nama organisasi tidak boleh kosong");
        }

        String trimmedName = name.trim();

        // Try to find existing organization
        return organizationRepository.findByNameIgnoreCase(trimmedName)
                .map(this::toDTO)
                .orElseGet(() -> {
                    // Create new organization if not found
                    log.info("Creating new organization: {}", trimmedName);
                    
                    // Double-check to avoid race condition
                    if (organizationRepository.findByNameIgnoreCase(trimmedName).isPresent()) {
                        throw new ConflictException("Organisasi dengan nama '" + trimmedName + "' sudah ada");
                    }
                    
                    Organization organization = new Organization(trimmedName);
                    Organization savedOrganization = organizationRepository.save(organization);
                    return toDTO(savedOrganization);
                });
    }

    private OrganizationDTO toDTO(Organization organization) {
        if (organization == null) {
            return null;
        }
        return new OrganizationDTO(organization.getId(), organization.getName());
    }
}
