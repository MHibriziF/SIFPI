package id.go.kemenkoinfra.ipfo.sifpi.auth.service.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import id.go.kemenkoinfra.ipfo.sifpi.auth.dto.RoleResponseDTO;
import id.go.kemenkoinfra.ipfo.sifpi.auth.dto.request.CreateRoleRequest;
import id.go.kemenkoinfra.ipfo.sifpi.auth.dto.request.PermissionRequest;
import id.go.kemenkoinfra.ipfo.sifpi.auth.dto.request.UpdateRoleRequest;
import id.go.kemenkoinfra.ipfo.sifpi.auth.mapper.RoleMapper;
import id.go.kemenkoinfra.ipfo.sifpi.auth.model.Resource;
import id.go.kemenkoinfra.ipfo.sifpi.auth.model.Role;
import id.go.kemenkoinfra.ipfo.sifpi.auth.model.RoleAuditLog;
import id.go.kemenkoinfra.ipfo.sifpi.auth.model.RolePermission;
import id.go.kemenkoinfra.ipfo.sifpi.auth.repository.ResourceRepository;
import id.go.kemenkoinfra.ipfo.sifpi.auth.repository.RoleAuditLogRepository;
import id.go.kemenkoinfra.ipfo.sifpi.auth.repository.RolePermissionRepository;
import id.go.kemenkoinfra.ipfo.sifpi.auth.repository.RoleRepository;
import id.go.kemenkoinfra.ipfo.sifpi.auth.service.RoleService;
import id.go.kemenkoinfra.ipfo.sifpi.common.enums.Action;
import id.go.kemenkoinfra.ipfo.sifpi.common.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    private static final String ACTION_UPDATE = "UPDATE";

    private final RoleRepository roleRepository;
    private final ResourceRepository resourceRepository;
    private final RolePermissionRepository rolePermissionRepository;
    private final RoleAuditLogRepository roleAuditLogRepository;
    private final RoleMapper roleMapper;

    @Override
    @Transactional
    public RoleResponseDTO createRole(CreateRoleRequest request) {
        if (roleRepository.findByName(request.getName()).isPresent()) {
            throw new IllegalStateException("Role dengan nama '" + request.getName() + "' sudah ada.");
        }

        Role role = roleMapper.toEntity(request);
        roleRepository.save(role);

        List<RolePermission> permissions = buildPermissions(role, request.getPermissions());
        rolePermissionRepository.saveAll(permissions);

        log.info("Role '{}' berhasil dibuat dengan {} izin.", role.getName(), permissions.size());

        return roleMapper.toDTO(role, permissions);
    }

    private List<RolePermission> buildPermissions(Role role, List<PermissionRequest> requests) {
        Set<String> seen = new HashSet<>();
        List<RolePermission> permissions = new ArrayList<>();

        for (PermissionRequest req : requests) {
            Resource resource = resourceRepository.findById(req.getResource())
                    .orElseThrow(() -> new NotFoundException("Resource", req.getResource()));

            for (Action action : req.getActions()) {
                String key = req.getResource() + ":" + action.name();
                if (!seen.add(key)) {
                    throw new IllegalArgumentException(
                            "Izin duplikat dalam permintaan: resource '" + req.getResource() + "' dengan aksi '" + action + "'.");
                }

                RolePermission rp = new RolePermission();
                rp.setRole(role);
                rp.setResource(resource);
                rp.setAction(action);
                permissions.add(rp);
            }
        }

        return permissions;
    }

    @Override
    @Transactional(readOnly = true)
    public RoleResponseDTO getRoleById(UUID id) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Role", id));

        List<RolePermission> permissions = rolePermissionRepository.findByRoleWithResource(role);
        log.info("Role detail fetched: id={}", id);
        return roleMapper.toDTO(role, permissions);
    }

    @Override
    @Transactional
    public RoleResponseDTO updateRole(UUID id, UpdateRoleRequest request) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Role", id));

        // Validate name uniqueness if name is being changed
        if (request.getName() != null && !request.getName().equals(role.getName())) {
            roleRepository.findByName(request.getName()).ifPresent(existing -> {
                throw new IllegalStateException("Role dengan nama '" + request.getName() + "' sudah ada.");
            });
        }

        // Capture before state for audit trail
        List<RolePermission> oldPermissions = rolePermissionRepository.findByRoleWithResource(role);
        String before = buildSnapshot(role, oldPermissions);

        // Patch scalar fields (null-safe via MapStruct IGNORE strategy)
        roleMapper.updateEntity(request, role);
        roleRepository.save(role);

        // Replace permissions only if the request explicitly provides the field
        List<RolePermission> newPermissions;
        if (request.getPermissions() != null) {
            rolePermissionRepository.deleteByRole(role);
            newPermissions = buildPermissions(role, request.getPermissions());
            rolePermissionRepository.saveAll(newPermissions);
        } else {
            newPermissions = rolePermissionRepository.findByRoleWithResource(role);
        }

        // Capture after state and persist audit log
        String after = buildSnapshot(role, newPermissions);
        String actor = SecurityContextHolder.getContext().getAuthentication().getName();
        saveAuditLog(role, ACTION_UPDATE, actor, buildChangeDetails(before, after));

        log.info("Role '{}' berhasil diperbarui oleh '{}'.", role.getName(), actor);
        return roleMapper.toDTO(role, newPermissions);
    }

    private void saveAuditLog(Role role, String action, String changedBy, String details) {
        roleAuditLogRepository.save(RoleAuditLog.builder()
                .role(role)
                .action(action)
                .changedBy(changedBy)
                .details(details)
                .build());
    }

    private String buildSnapshot(Role role, List<RolePermission> permissions) {
        String perms = permissions.stream()
                .map(rp -> rp.getResourceName() + ":" + rp.getActionName())
                .sorted()
                .collect(Collectors.joining(", "));
        return "name=" + role.getName()
                + ", description=" + role.getDescription()
                + ", status=" + role.isStatus()
                + ", permissions=[" + perms + "]";
    }

    private String buildChangeDetails(String before, String after) {
        return "BEFORE: {" + before + "} | AFTER: {" + after + "}";
    }
}
