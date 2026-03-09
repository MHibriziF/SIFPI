package id.go.kemenkoinfra.ipfo.sifpi.auth.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RoleUserDTO {

    private String nama;
    private String email;

    // Investor-only fields
    private String organisasi;
    private List<String> sectorInterest;
    private String budgetRange;
}
