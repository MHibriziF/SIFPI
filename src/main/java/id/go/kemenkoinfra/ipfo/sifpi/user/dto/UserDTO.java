package id.go.kemenkoinfra.ipfo.sifpi.user.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {

    private String email;

    @JsonProperty("nama")
    private String name;

    private String organisasi;

    private String phone;

    private String role;

    @JsonProperty("is_verified")
    private Boolean isVerified;

    @JsonProperty("is_active")
    private Boolean isActive;

    @JsonProperty("created_at")
    private LocalDateTime createdAt;
}
