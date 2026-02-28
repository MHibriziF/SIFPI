package id.go.kemenkoinfra.ipfo.sifpi.common.config;

import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import id.go.kemenkoinfra.ipfo.sifpi.auth.model.Resource;
import id.go.kemenkoinfra.ipfo.sifpi.auth.model.Role;
import id.go.kemenkoinfra.ipfo.sifpi.auth.model.RolePermission;
import id.go.kemenkoinfra.ipfo.sifpi.auth.model.User;
import id.go.kemenkoinfra.ipfo.sifpi.auth.repository.ResourceRepository;
import id.go.kemenkoinfra.ipfo.sifpi.auth.repository.RolePermissionRepository;
import id.go.kemenkoinfra.ipfo.sifpi.auth.repository.RoleRepository;
import id.go.kemenkoinfra.ipfo.sifpi.auth.repository.UserRepository;
import id.go.kemenkoinfra.ipfo.sifpi.common.enums.Action;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class DataSeeder {

    private static final List<String> DEFAULT_ROLES = List.of(
            "ADMIN", "INVESTOR", "PROJECT_OWNER", "EXECUTIVE"
    );

    private static final List<String> DEFAULT_RESOURCES = List.of(
            "PROJECT",
            "USER",
            "INQUIRY",
            "NEWS",
            "VERIFICATION"
    );

    private final RoleRepository roleRepository;
    private final ResourceRepository resourceRepository;
    private final RolePermissionRepository rolePermissionRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Bean
    CommandLineRunner seedData() {
        return args -> {
            seedRoles();
            seedResources();
            seedAdminPermissions();
            seedAdmin();
        };
    }

    private void seedRoles() {
        for (String roleName : DEFAULT_ROLES) {
            roleRepository.findByName(roleName).orElseGet(() -> {
                Role role = new Role();
                role.setName(roleName);
                role.setDescription("Role untuk " + roleName + "mengelola segala hal");
                roleRepository.save(role);
                log.info("Role dibuat: {}", roleName);
                return role;
            });
        }
    }

    private void seedResources() {
        for (String resourceName : DEFAULT_RESOURCES) {
            resourceRepository.findById(resourceName).orElseGet(() -> {
                Resource resource = new Resource();
                resource.setName(resourceName);
                resourceRepository.save(resource);
                log.info("Resource dibuat: {}", resourceName);
                return resource;
            });
        }
    }

    private void seedAdminPermissions() {
        Role adminRole = roleRepository.findByName("ADMIN")
                .orElseThrow(() -> new IllegalStateException("Role ADMIN belum ada."));

        for (String resourceName : DEFAULT_RESOURCES) {
            Resource resource = resourceRepository.findById(resourceName)
                    .orElseThrow(() -> new IllegalStateException("Resource " + resourceName + " belum ada."));

            for (Action action : Action.values()) {
                if (!rolePermissionRepository.existsByRoleAndResourceAndAction(adminRole, resource, action)) {
                    RolePermission rp = new RolePermission();
                    rp.setRole(adminRole);
                    rp.setResource(resource);
                    rp.setAction(action);
                    rolePermissionRepository.save(rp);
                    log.info("Permission ADMIN dibuat: {}:{}", resourceName, action);
                }
            }
        }
    }

    private void seedAdmin() {
        if (userRepository.existsByEmail("admin@sifpi.go.id")) {
            return;
        }

        Role adminRole = roleRepository.findByName("ADMIN")
                .orElseThrow(() -> new IllegalStateException("Role ADMIN belum ada."));

        User admin = new User();
        admin.setEmail("admin@sifpi.go.id");
        admin.setPassword(passwordEncoder.encode("admin123"));
        admin.setName("Administrator");
        admin.setRole(adminRole);

        userRepository.save(admin);
        log.info("User admin default dibuat: admin@sifpi.go.id");
    }
}
