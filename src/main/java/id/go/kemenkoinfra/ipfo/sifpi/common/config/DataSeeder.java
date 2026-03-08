package id.go.kemenkoinfra.ipfo.sifpi.common.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
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
            "ADMIN", "INVESTOR", "PROJECT_OWNER", "EXECUTIVE", "PUBLIC"
    );

    private static final List<String> DEFAULT_RESOURCES = List.of(
            "PROJECT",
            "USER",
            "INQUIRY",
            "NEWS",
            "VERIFICATION"
    );

    private static final List<String> PUBLIC_READ_RESOURCES = List.of(
            "PROJECT",
            "NEWS"
    );

    private final RoleRepository roleRepository;
    private final ResourceRepository resourceRepository;
    private final RolePermissionRepository rolePermissionRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.seed.password:password123}")
    private String seedPassword;

    @Bean
    CommandLineRunner seedData() {
        return args -> {
            seedRoles();
            seedResources();
            seedAdminPermissions();
            seedProjectOwnerPermissions();
            seedPublicPermissions();
            seedAdmin();
            seedUsers();
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

    private void seedProjectOwnerPermissions() {
        Role projectOwnerRole = roleRepository.findByName("PROJECT_OWNER")
                .orElseThrow(() -> new IllegalStateException("Role PROJECT_OWNER belum ada."));

        seedProjectOwnerPermission(projectOwnerRole, "PROJECT", Action.CREATE);
        seedProjectOwnerPermission(projectOwnerRole, "PROJECT", Action.READ);
        seedProjectOwnerPermission(projectOwnerRole, "PROJECT", Action.UPDATE);
        seedProjectOwnerPermission(projectOwnerRole, "NEWS", Action.READ);
    }

    private void seedProjectOwnerPermission(Role projectOwnerRole, String resourceName, Action action) {
        Resource resource = resourceRepository.findById(resourceName)
                .orElseThrow(() -> new IllegalStateException("Resource " + resourceName + " belum ada."));

        if (!rolePermissionRepository.existsByRoleAndResourceAndAction(projectOwnerRole, resource, action)) {
            RolePermission rp = new RolePermission();
            rp.setRole(projectOwnerRole);
            rp.setResource(resource);
            rp.setAction(action);
            rolePermissionRepository.save(rp);
            log.info("Permission PROJECT_OWNER dibuat: {}:{}", resourceName, action);
        }
    }

    private void seedPublicPermissions() {
        Role publicRole = roleRepository.findByName("PUBLIC")
                .orElseThrow(() -> new IllegalStateException("Role PUBLIC belum ada."));

        for (String resourceName : PUBLIC_READ_RESOURCES) {
            Resource resource = resourceRepository.findById(resourceName)
                    .orElseThrow(() -> new IllegalStateException("Resource " + resourceName + " belum ada."));

            if (!rolePermissionRepository.existsByRoleAndResourceAndAction(publicRole, resource, Action.READ)) {
                RolePermission rp = new RolePermission();
                rp.setRole(publicRole);
                rp.setResource(resource);
                rp.setAction(Action.READ);
                rolePermissionRepository.save(rp);
                log.info("Permission PUBLIC dibuat: {}:{}", resourceName, Action.READ);
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
        admin.setPassword(passwordEncoder.encode(seedPassword));
        admin.setName("Administrator");
        admin.setRole(adminRole);
        admin.setEmailVerified(true);

        userRepository.save(admin);
        log.info("User admin default dibuat: admin@sifpi.go.id");
    }

    private void seedUsers() {
        if (!userRepository.existsByEmail("projectowner@sifpi.go.id")) {
            User projectOwner = new User();
            projectOwner.setEmail("projectowner@sifpi.go.id");
            projectOwner.setPassword(passwordEncoder.encode(seedPassword));
            projectOwner.setName("Project Owner");
            projectOwner.setRole(roleRepository.findByName("PROJECT_OWNER")
                    .orElseThrow(() -> new IllegalStateException("Role PROJECT_OWNER belum ada.")));
            projectOwner.setEmailVerified(true);
            userRepository.save(projectOwner);
            log.info("User project owner default dibuat: projectowner@sifpi.go.id");
        }

        if (!userRepository.existsByEmail("executive@sifpi.go.id")) {
            User executive = new User();
            executive.setEmail("executive@sifpi.go.id");
            executive.setPassword(passwordEncoder.encode(seedPassword));
            executive.setName("Executive");
            executive.setRole(roleRepository.findByName("EXECUTIVE")
                    .orElseThrow(() -> new IllegalStateException("Role EXECUTIVE belum ada.")));
            executive.setEmailVerified(true);
            userRepository.save(executive);
            log.info("User executive default dibuat: executive@sifpi.go.id");
        }

        if (!userRepository.existsByEmail("investor@pt.com")) {
            User investor = new User();
            investor.setEmail("investor@pt.com");
            investor.setPassword(passwordEncoder.encode(seedPassword));
            investor.setName("Investor");
            investor.setRole(roleRepository.findByName("INVESTOR")
                    .orElseThrow(() -> new IllegalStateException("Role INVESTOR belum ada.")));
            investor.setEmailVerified(true);
            userRepository.save(investor);
            log.info("User investor default dibuat: investor@pt.com");
        }
    }
}
