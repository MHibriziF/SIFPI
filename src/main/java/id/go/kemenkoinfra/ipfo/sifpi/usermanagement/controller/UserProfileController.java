package id.go.kemenkoinfra.ipfo.sifpi.usermanagement.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import id.go.kemenkoinfra.ipfo.sifpi.common.dto.BaseResponseDTO;
import id.go.kemenkoinfra.ipfo.sifpi.common.utils.ResponseUtil;
import id.go.kemenkoinfra.ipfo.sifpi.usermanagement.dto.request.BatchUpdateUserRoleRequest;
import id.go.kemenkoinfra.ipfo.sifpi.usermanagement.dto.BatchUpdateUserRoleResultDTO;
import id.go.kemenkoinfra.ipfo.sifpi.usermanagement.dto.request.UpdateProfileRequest;
import id.go.kemenkoinfra.ipfo.sifpi.usermanagement.dto.UserDetailDTO;
import id.go.kemenkoinfra.ipfo.sifpi.usermanagement.service.UpdateProfileService;
import id.go.kemenkoinfra.ipfo.sifpi.usermanagement.service.UpdateUserRoleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserProfileController {

    private final UpdateProfileService updateProfileService;
    private final UpdateUserRoleService updateUserRoleService;
    private final ResponseUtil responseUtil;

    /**
     * UM-8 — Update own profile.
     * The caller's identity is taken from the JWT (no ID in the path).
     * Role-specific fields (institution, company, sectors) are applied only when
     * the authenticated user holds the matching role; extra fields are silently ignored.
     * <p>
     * PATCH /api/users/profile
     */
    @PatchMapping("/profile")
    public ResponseEntity<BaseResponseDTO<UserDetailDTO>> updateProfile(
            @AuthenticationPrincipal String email,
            @Valid @RequestBody UpdateProfileRequest request) {

        UserDetailDTO result = updateProfileService.updateProfile(email, request);
        return responseUtil.success(result, "Profil berhasil diperbarui.", HttpStatus.OK);
    }

    /**
     * Batch update role for multiple users.
     * <p>
     * PATCH /api/users/roles
     */
    @PatchMapping("/roles")
    @PreAuthorize("hasAuthority('USER:UPDATE')")
    public ResponseEntity<BaseResponseDTO<BatchUpdateUserRoleResultDTO>> batchUpdateUserRole(
            @Valid @RequestBody BatchUpdateUserRoleRequest request,
            Authentication authentication) {

        String adminEmail = authentication.getName();
        BatchUpdateUserRoleResultDTO result = updateUserRoleService.batchUpdateUserRole(request, adminEmail);
        return responseUtil.success(result, "Batch update role berhasil diproses.", HttpStatus.OK);
    }
}
