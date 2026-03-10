package id.go.kemenkoinfra.ipfo.sifpi.usermanagement.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class BulkInsertUserRequest {

    private String email;
    private String nama;
    private String role;
    private String organisasi;
    private String phone;

    @JsonProperty("is_active")
    private Boolean isActive;
}
