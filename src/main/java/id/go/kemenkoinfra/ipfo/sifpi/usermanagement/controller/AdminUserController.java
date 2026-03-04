package id.go.kemenkoinfra.ipfo.sifpi.usermanagement.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import id.go.kemenkoinfra.ipfo.sifpi.common.dto.BaseResponseDTO;
import id.go.kemenkoinfra.ipfo.sifpi.common.utils.ResponseUtil;
import id.go.kemenkoinfra.ipfo.sifpi.usermanagement.dto.CreateExecutiveRequest;
import id.go.kemenkoinfra.ipfo.sifpi.usermanagement.dto.ExecutiveDTO;
import id.go.kemenkoinfra.ipfo.sifpi.usermanagement.service.ExecutiveService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AdminUserController {

    private final ExecutiveService executiveService;
    private final ResponseUtil responseUtil;

    @PostMapping("/register")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BaseResponseDTO<ExecutiveDTO>> createExecutive(
            @Valid @RequestBody CreateExecutiveRequest request) {

        ExecutiveDTO result = executiveService.createExecutive(request);
        return responseUtil.success(result, "Akun Executive berhasil dibuat.", HttpStatus.CREATED);
    }
}
