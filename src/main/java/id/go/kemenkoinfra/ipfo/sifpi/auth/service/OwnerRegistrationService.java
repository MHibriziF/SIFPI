package id.go.kemenkoinfra.ipfo.sifpi.auth.service;

import id.go.kemenkoinfra.ipfo.sifpi.auth.dto.OwnerDTO;
import id.go.kemenkoinfra.ipfo.sifpi.auth.dto.request.CreateOwnerRequest;

/**
 * Service interface for project owner registration.
 */
public interface OwnerRegistrationService {

    /**
     * Register a new project owner.
     * 
     * @param request the owner registration request
     * @return the registered owner DTO
     */
    OwnerDTO registerOwner(CreateOwnerRequest request);
}
