package id.go.kemenkoinfra.ipfo.sifpi.usermanagement.service;

import id.go.kemenkoinfra.ipfo.sifpi.usermanagement.dto.CreateExecutiveRequest;
import id.go.kemenkoinfra.ipfo.sifpi.usermanagement.dto.ExecutiveDTO;

/**
 * Service interface for managing Executive users.
 */
public interface ExecutiveService {

    /**
     * Create a new executive account.
     * Generates a password setup token and sends invitation email.
     * 
     * @param request the executive creation request
     * @return the created executive DTO
     * @throws ConflictException if email already exists
     * @throws NotFoundException if EXECUTIVE role not found
     */
    ExecutiveDTO createExecutive(CreateExecutiveRequest request);
}
