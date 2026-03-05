package id.go.kemenkoinfra.ipfo.sifpi.auth.service;

import id.go.kemenkoinfra.ipfo.sifpi.auth.dto.InvestorDTO;
import id.go.kemenkoinfra.ipfo.sifpi.auth.dto.request.CreateInvestorRequest;

/**
 * Service interface for investor registration.
 */
public interface InvestorRegistrationService {

    /**
     * Register a new investor.
     * 
     * @param request the investor registration request
     * @return the registered investor DTO
     */
    InvestorDTO registerInvestor(CreateInvestorRequest request);
}
