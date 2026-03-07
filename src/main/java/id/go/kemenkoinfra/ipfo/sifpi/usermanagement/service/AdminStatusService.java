package id.go.kemenkoinfra.ipfo.sifpi.usermanagement.service;

import id.go.kemenkoinfra.ipfo.sifpi.usermanagement.dto.UpdateUserStatusRequest;
import id.go.kemenkoinfra.ipfo.sifpi.usermanagement.dto.UserStatusResponseDTO;

public interface AdminStatusService {
    UserStatusResponseDTO updateUserStatus(String email, UpdateUserStatusRequest request, String adminEmail);
}
