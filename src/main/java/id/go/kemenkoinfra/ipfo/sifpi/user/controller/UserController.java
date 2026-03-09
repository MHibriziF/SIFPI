package id.go.kemenkoinfra.ipfo.sifpi.user.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import id.go.kemenkoinfra.ipfo.sifpi.common.dto.BaseResponseDTO;
import id.go.kemenkoinfra.ipfo.sifpi.common.dto.PagedResponseDTO;
import id.go.kemenkoinfra.ipfo.sifpi.common.utils.ResponseUtil;
import id.go.kemenkoinfra.ipfo.sifpi.user.dto.UserDTO;
import id.go.kemenkoinfra.ipfo.sifpi.user.service.UserService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final ResponseUtil responseUtil;

    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BaseResponseDTO<PagedResponseDTO<UserDTO>>> getAllUsers(
            @PageableDefault(size = 4) Pageable pageable,
            @RequestParam(required = false) String role,
            @RequestParam(required = false) Boolean isVerified,
            @RequestParam(required = false) Boolean isActive,
            @RequestParam(required = false) String organisasi,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false, defaultValue = "asc") String sortDirection) {

        Page<UserDTO> userPage = userService.getAllUsers(
                pageable, role, isVerified, isActive, organisasi, search, sortBy, sortDirection);
        return responseUtil.successPaged(userPage, "Berhasil.", HttpStatus.OK);
    }
}

