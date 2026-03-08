package id.go.kemenkoinfra.ipfo.sifpi.auth.service;

import id.go.kemenkoinfra.ipfo.sifpi.auth.dto.VerificationResponseDTO;

public interface AdminVerificationService {
    
    VerificationResponseDTO verifyProjectOwner(String projectOwnerEmail, String adminEmail);
}
