package id.go.kemenkoinfra.ipfo.sifpi.usermanagement.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import id.go.kemenkoinfra.ipfo.sifpi.common.dto.BaseResponseDTO;
import id.go.kemenkoinfra.ipfo.sifpi.common.utils.ResponseUtil;
import id.go.kemenkoinfra.ipfo.sifpi.usermanagement.dto.BulkInsertResultDTO;
import id.go.kemenkoinfra.ipfo.sifpi.usermanagement.dto.BulkInsertUserRequest;
import id.go.kemenkoinfra.ipfo.sifpi.usermanagement.dto.CreateExecutiveRequest;
import id.go.kemenkoinfra.ipfo.sifpi.usermanagement.dto.ExecutiveDTO;
import id.go.kemenkoinfra.ipfo.sifpi.usermanagement.dto.UpdateUserStatusRequest;
import id.go.kemenkoinfra.ipfo.sifpi.usermanagement.dto.UserStatusResponseDTO;
import id.go.kemenkoinfra.ipfo.sifpi.usermanagement.service.AdminStatusService;
import id.go.kemenkoinfra.ipfo.sifpi.usermanagement.dto.UserDetailDTO;
import id.go.kemenkoinfra.ipfo.sifpi.usermanagement.service.AdminUserDetailService;
import id.go.kemenkoinfra.ipfo.sifpi.usermanagement.service.BulkUserImportService;
import id.go.kemenkoinfra.ipfo.sifpi.usermanagement.service.ExecutiveService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AdminUserController {


    private final ExecutiveService executiveService;
    private final BulkUserImportService bulkUserImportService;
    private final AdminStatusService adminStatusService;
    private final AdminUserDetailService adminUserDetailService;
    private final ResponseUtil responseUtil;

    @PostMapping("/register/executive")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BaseResponseDTO<ExecutiveDTO>> createExecutive(
            @Valid @RequestBody CreateExecutiveRequest request) {

        ExecutiveDTO result = executiveService.createExecutive(request);
        return responseUtil.success(result, "Akun Executive berhasil dibuat.", HttpStatus.CREATED);
    }

    @PostMapping("/admin/users/bulk")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BaseResponseDTO<BulkInsertResultDTO>> bulkInsertUsers(
            @RequestBody List<BulkInsertUserRequest> requests) {

        BulkInsertResultDTO result = bulkUserImportService.bulkInsertUsers(requests);
        return responseUtil.success(result, "Bulk insert user berhasil.", HttpStatus.CREATED);
    }

    @PatchMapping("/admin/users/{email}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BaseResponseDTO<UserStatusResponseDTO>> updateUserStatus(
            @PathVariable String email,
            @Valid @RequestBody UpdateUserStatusRequest request,
            Authentication authentication) {

        String adminEmail = authentication.getName();
        UserStatusResponseDTO result = adminStatusService.updateUserStatus(email, request, adminEmail);
        return responseUtil.success(result, "Status user berhasil diubah.", HttpStatus.OK);
    }

    /**
     * UM-7 — Read detail account (admin only).
     * The {email:.+} pattern ensures dots in the email are not truncated by Spring MVC.
     */
    @GetMapping("/admin/users/{email:.+}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BaseResponseDTO<UserDetailDTO>> getUserDetail(
            @PathVariable String email) {

        UserDetailDTO detail = adminUserDetailService.getUserDetailByEmail(email);
        return responseUtil.success(detail, "Berhasil.", HttpStatus.OK);
    }
}
