package id.go.kemenkoinfra.ipfo.sifpi.auth.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
public class InvestorDTO {
    private UUID id;
    private String nama;
    private String email;
    private String phone;
    private String organisasi;
    private String jabatan;
    private Boolean isVerified;
    private Boolean isActive;
    private String roleName;
    
    // Investor specific fields
    private String budgetInvestasi;
    private List<String> sectorInterest;
    private Boolean optInEmail;
    private Boolean agreePrivacy;
    
    private LocalDateTime createdAt;
}
