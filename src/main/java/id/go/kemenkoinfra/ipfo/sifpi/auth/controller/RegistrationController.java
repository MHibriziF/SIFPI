package id.go.kemenkoinfra.ipfo.sifpi.auth.controller;

import id.go.kemenkoinfra.ipfo.sifpi.auth.dto.InvestorDTO;
import id.go.kemenkoinfra.ipfo.sifpi.auth.dto.OwnerDTO;
import id.go.kemenkoinfra.ipfo.sifpi.auth.dto.request.CreateInvestorRequest;
import id.go.kemenkoinfra.ipfo.sifpi.auth.dto.request.CreateOwnerRequest;
import id.go.kemenkoinfra.ipfo.sifpi.auth.service.InvestorRegistrationService;
import id.go.kemenkoinfra.ipfo.sifpi.auth.service.OwnerRegistrationService;
import id.go.kemenkoinfra.ipfo.sifpi.common.dto.BaseResponseDTO;
import id.go.kemenkoinfra.ipfo.sifpi.common.utils.ResponseUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth/register")
@RequiredArgsConstructor
public class RegistrationController {

    private final OwnerRegistrationService ownerRegistrationService;
    private final InvestorRegistrationService investorRegistrationService;
    private final ResponseUtil responseUtil;

    @PostMapping("/owner")
    public ResponseEntity<BaseResponseDTO<OwnerDTO>> registerOwner(
            @Valid @RequestBody CreateOwnerRequest request) {

        OwnerDTO result = ownerRegistrationService.registerOwner(request);
        return responseUtil.success(result, "Berhasil mendaftar sebagai Project Owner.", HttpStatus.CREATED);
    }

    @PostMapping("/investor")
    public ResponseEntity<BaseResponseDTO<InvestorDTO>> registerInvestor(
            @Valid @RequestBody CreateInvestorRequest request) {

        InvestorDTO result = investorRegistrationService.registerInvestor(request);
        return responseUtil.success(result, "Berhasil mendaftar sebagai Investor.", HttpStatus.CREATED);
    }
}
