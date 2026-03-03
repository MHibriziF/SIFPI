package id.go.kemenkoinfra.ipfo.sifpi.auth.controller;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import id.go.kemenkoinfra.ipfo.sifpi.auth.dto.request.CreateRoleRequest;
import id.go.kemenkoinfra.ipfo.sifpi.auth.dto.RoleResponseDTO;
import id.go.kemenkoinfra.ipfo.sifpi.auth.service.RoleService;
import id.go.kemenkoinfra.ipfo.sifpi.common.constants.AuthPath;
import id.go.kemenkoinfra.ipfo.sifpi.common.dto.BaseResponseDTO;
import id.go.kemenkoinfra.ipfo.sifpi.common.utils.ResponseUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;
    private final ResponseUtil responseUtil;

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
}
