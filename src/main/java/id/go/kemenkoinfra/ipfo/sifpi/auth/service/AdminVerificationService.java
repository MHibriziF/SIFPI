package id.go.kemenkoinfra.ipfo.sifpi.auth.service;

import id.go.kemenkoinfra.ipfo.sifpi.auth.dto.VerificationResponseDTO;
import id.go.kemenkoinfra.ipfo.sifpi.auth.dto.request.VerifyOwnerRequest;

public interface AdminVerificationService {
    
    VerificationResponseDTO verifyProjectOwner(VerifyOwnerRequest request, String adminEmail);
}
