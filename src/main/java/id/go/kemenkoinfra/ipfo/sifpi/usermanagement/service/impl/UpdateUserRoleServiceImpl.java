package id.go.kemenkoinfra.ipfo.sifpi.usermanagement.service.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import id.go.kemenkoinfra.ipfo.sifpi.auth.model.Role;
import id.go.kemenkoinfra.ipfo.sifpi.auth.model.User;
import id.go.kemenkoinfra.ipfo.sifpi.auth.repository.RoleRepository;
import id.go.kemenkoinfra.ipfo.sifpi.auth.repository.UserRepository;
import id.go.kemenkoinfra.ipfo.sifpi.usermanagement.dto.BatchRoleUpdateErrorDTO;
import id.go.kemenkoinfra.ipfo.sifpi.usermanagement.dto.request.BatchUpdateUserRoleRequest;
import id.go.kemenkoinfra.ipfo.sifpi.usermanagement.dto.BatchUpdateUserRoleResultDTO;
import id.go.kemenkoinfra.ipfo.sifpi.usermanagement.dto.request.UserRoleUpdateItem;
import id.go.kemenkoinfra.ipfo.sifpi.usermanagement.service.UpdateUserRoleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class UpdateUserRoleServiceImpl implements UpdateUserRoleService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    @Override
    @Transactional
    public BatchUpdateUserRoleResultDTO batchUpdateUserRole(BatchUpdateUserRoleRequest request, String adminEmail) {
        List<UserRoleUpdateItem> updates = request.getUpdates();

        // Fetch all required roles in one query
        Set<String> roleNames = updates.stream()
                .map(UserRoleUpdateItem::getRoleName)
                .collect(Collectors.toSet());
        Map<String, Role> roleByName = roleRepository.findByNameIn(roleNames).stream()
                .collect(Collectors.toMap(Role::getName, r -> r));

        // Fetch all required users in one query
        Set<String> emails = updates.stream()
                .map(item -> item.getEmail().toLowerCase())
                .collect(Collectors.toSet());
        Map<String, User> userByEmail = userRepository.findAllByEmailInIgnoreCase(emails).stream()
                .collect(Collectors.toMap(u -> u.getEmail().toLowerCase(), u -> u));

        List<User> toSave = new ArrayList<>();
        List<BatchRoleUpdateErrorDTO> errors = new ArrayList<>();

        for (UserRoleUpdateItem item : updates) {
            String emailKey = item.getEmail().toLowerCase();
            User user = userByEmail.get(emailKey);
            if (user == null) {
                errors.add(new BatchRoleUpdateErrorDTO(item.getEmail(), "User tidak ditemukan"));
                continue;
            }

            Role role = roleByName.get(item.getRoleName());
            if (role == null) {
                errors.add(new BatchRoleUpdateErrorDTO(item.getEmail(),
                        "Role '" + item.getRoleName() + "' tidak ditemukan"));
                continue;
            }

            user.setRole(role);
            user.setChangedBy(adminEmail);
            user.setChangedAt(LocalDateTime.now());
            user.setAction("ROLE_UPDATED");
            toSave.add(user);
        }

        if (!toSave.isEmpty()) {
            userRepository.saveAllAndFlush(toSave);
            log.info("Batch update role: {} user berhasil diperbarui oleh {}", toSave.size(), adminEmail);
        }

        return new BatchUpdateUserRoleResultDTO(updates.size(), toSave.size(), errors);
    }
}
