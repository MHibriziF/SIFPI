package id.go.kemenkoinfra.ipfo.sifpi.auth.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import id.go.kemenkoinfra.ipfo.sifpi.auth.dto.RoleResponseDTO;
import id.go.kemenkoinfra.ipfo.sifpi.auth.dto.RoleSummaryDTO;
import id.go.kemenkoinfra.ipfo.sifpi.auth.dto.RoleUserDTO;
import id.go.kemenkoinfra.ipfo.sifpi.auth.dto.request.CreateRoleRequest;
import id.go.kemenkoinfra.ipfo.sifpi.auth.dto.request.UpdateRoleRequest;
import id.go.kemenkoinfra.ipfo.sifpi.auth.service.RoleService;
import id.go.kemenkoinfra.ipfo.sifpi.common.constants.AuthPath;
import id.go.kemenkoinfra.ipfo.sifpi.common.dto.BaseResponseDTO;
import id.go.kemenkoinfra.ipfo.sifpi.common.dto.PagedResponseDTO;
import id.go.kemenkoinfra.ipfo.sifpi.common.utils.ResponseUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;
    private final ResponseUtil responseUtil;

    @GetMapping(AuthPath.ROLES)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BaseResponseDTO<List<RoleSummaryDTO>>> getAllRoles() {
        List<RoleSummaryDTO> result = roleService.getAllRoles();
        return responseUtil.success(result, "Daftar role berhasil diambil.", HttpStatus.OK);
    }

    @GetMapping(AuthPath.ROLES + "/{id}/users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BaseResponseDTO<PagedResponseDTO<RoleUserDTO>>> getUsersByRole(
            @PathVariable UUID id,
            @RequestParam(required = false, defaultValue = "") String search,
            @PageableDefault(size = 20, sort = "name", direction = Sort.Direction.ASC) Pageable pageable) {

        Page<RoleUserDTO> result = roleService.getUsersByRole(id, search, pageable);
        return responseUtil.successPaged(result, "Daftar pengguna berhasil diambil.", HttpStatus.OK);
    }

    @PostMapping(AuthPath.ROLES)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BaseResponseDTO<RoleResponseDTO>> createRole(
            @Valid @RequestBody CreateRoleRequest request) {

        RoleResponseDTO dto = roleService.createRole(request);
        return responseUtil.success(dto, "Role berhasil dibuat.", HttpStatus.CREATED);
    }

    @GetMapping(AuthPath.ROLES + "/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BaseResponseDTO<RoleResponseDTO>> getRoleById(@PathVariable UUID id) {
        RoleResponseDTO dto = roleService.getRoleById(id);
        return responseUtil.success(dto, "Detail role berhasil diambil.", HttpStatus.OK);
    }

    @PutMapping(AuthPath.ROLES + "/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BaseResponseDTO<RoleResponseDTO>> updateRole(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateRoleRequest request) {

        RoleResponseDTO dto = roleService.updateRole(id, request);
        return responseUtil.success(dto, "Role berhasil diperbarui.", HttpStatus.OK);
    }
}
