package id.go.kemenkoinfra.ipfo.sifpi.user.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import id.go.kemenkoinfra.ipfo.sifpi.user.dto.UserDTO;

public interface UserService {

    Page<UserDTO> getAllUsers(Pageable pageable, String role, Boolean isVerified,
                              Boolean isActive, String organisasi, String search,
                              String sortBy, String sortDirection);
}
