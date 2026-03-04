package id.go.kemenkoinfra.ipfo.sifpi.usermanagement.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import id.go.kemenkoinfra.ipfo.sifpi.common.dto.BaseResponseDTO;
import id.go.kemenkoinfra.ipfo.sifpi.common.utils.ResponseUtil;
import id.go.kemenkoinfra.ipfo.sifpi.usermanagement.dto.SetPasswordRequest;
import id.go.kemenkoinfra.ipfo.sifpi.usermanagement.dto.UpdatePasswordRequest;
import id.go.kemenkoinfra.ipfo.sifpi.usermanagement.service.PasswordService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class PasswordController {

    private final PasswordService passwordService;
    private final ResponseUtil responseUtil;

    /**
     * Set password for first time (from email invitation link).
     * This is a PUBLIC endpoint - no authentication required.
     * POST /api/auth/set-password
     */
    @PostMapping("/auth/set-password")
    public ResponseEntity<BaseResponseDTO<Void>> setPassword(
            @Valid @RequestBody SetPasswordRequest request) {
        
        passwordService.setPassword(request);
        return responseUtil.success(null, "Password berhasil dibuat. Silakan login.", HttpStatus.OK);
    }

    /**
     * Update password for logged-in user (UM-9).
     * PATCH /api/users/password
     */
    @PatchMapping("/users/password")
    public ResponseEntity<BaseResponseDTO<Void>> updatePassword(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody UpdatePasswordRequest request) {
        
        passwordService.updatePassword(userDetails.getUsername(), request);
        return responseUtil.success(null, "Password berhasil diubah.", HttpStatus.OK);
    }
}
