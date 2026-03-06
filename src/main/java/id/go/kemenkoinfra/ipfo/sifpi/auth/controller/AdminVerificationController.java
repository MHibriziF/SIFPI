package id.go.kemenkoinfra.ipfo.sifpi.auth.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import id.go.kemenkoinfra.ipfo.sifpi.auth.dto.VerificationResponseDTO;
import id.go.kemenkoinfra.ipfo.sifpi.auth.dto.request.VerifyOwnerRequest;
import id.go.kemenkoinfra.ipfo.sifpi.auth.service.AdminVerificationService;
import id.go.kemenkoinfra.ipfo.sifpi.common.dto.BaseResponseDTO;
import id.go.kemenkoinfra.ipfo.sifpi.common.utils.ResponseUtil;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminVerificationController {

    private final AdminVerificationService adminVerificationService;
    private final ResponseUtil responseUtil;

    /**
     * Admin verify a Project Owner account
     * PATCH /api/admin/users/:email/verify
     */
    @PatchMapping("/admin/users/{email}/verify")
    public ResponseEntity<BaseResponseDTO<VerificationResponseDTO>> verifyProjectOwner(
            @PathVariable String email,
            Authentication authentication) {

        VerifyOwnerRequest request = new VerifyOwnerRequest();
        request.setEmail(email);

        String adminEmail = authentication.getName();
        VerificationResponseDTO result = adminVerificationService.verifyProjectOwner(request, adminEmail);
        return responseUtil.success(result, "Project Owner berhasil diverifikasi.", HttpStatus.OK);
    }
}
