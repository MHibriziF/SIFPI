package id.go.kemenkoinfra.ipfo.sifpi.usermanagement.service.impl;

import id.go.kemenkoinfra.ipfo.sifpi.auth.model.User;
import id.go.kemenkoinfra.ipfo.sifpi.auth.repository.UserRepository;
import id.go.kemenkoinfra.ipfo.sifpi.common.exception.NotFoundException;
import id.go.kemenkoinfra.ipfo.sifpi.usermanagement.dto.UpdateUserStatusRequest;
import id.go.kemenkoinfra.ipfo.sifpi.usermanagement.dto.UserStatusResponseDTO;
import id.go.kemenkoinfra.ipfo.sifpi.usermanagement.mapper.UserStatusMapper;
import id.go.kemenkoinfra.ipfo.sifpi.usermanagement.service.AdminStatusService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminStatusServiceImpl implements AdminStatusService {

    private final UserRepository userRepository;
    private final UserStatusMapper userStatusMapper;

    @Override
    @Transactional
    public UserStatusResponseDTO updateUserStatus(String email, UpdateUserStatusRequest request, String adminEmail) {
        // Find user by email
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.warn("User tidak ditemukan dengan email: {}", email);
                    return new NotFoundException("User dengan email " + email + " tidak ditemukan");
                });

        // Set previous active status
        boolean previousActive = user.isActive();

        // Update status
        user.setActive(request.getIsActive());
        user.setChangedBy(adminEmail);
        user.setChangedAt(LocalDateTime.now());
        
        // Set action based on status change
        if (previousActive && !request.getIsActive()) {
            user.setAction("DEACTIVATED");
        } else if (!previousActive && request.getIsActive()) {
            user.setAction("ACTIVATED");
        }

        // Save user
        User updatedUser = userRepository.save(user);
        log.info("User {} status berhasil diubah menjadi {} oleh admin {}", 
                 email, request.getIsActive() ? "ACTIVE" : "INACTIVE", adminEmail);

        // Map to response DTO
        return userStatusMapper.toDTO(updatedUser);
    }
}
