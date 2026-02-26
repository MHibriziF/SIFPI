package id.go.kemenkoinfra.ipfo.sifpi.common.config;

import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import id.go.kemenkoinfra.ipfo.sifpi.auth.model.Role;
import id.go.kemenkoinfra.ipfo.sifpi.auth.model.User;
import id.go.kemenkoinfra.ipfo.sifpi.auth.repository.RoleRepository;
import id.go.kemenkoinfra.ipfo.sifpi.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class DataSeeder {

    private static final List<String> DEFAULT_ROLES = List.of(
            "ADMIN", "INVESTOR", "PROJECT_OWNER", "EXECUTIVE"
    );

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Bean
    CommandLineRunner seedData() {
        return args -> {
            seedRoles();
            seedAdmin();
        };
    }

    private void seedRoles() {
        for (String roleName : DEFAULT_ROLES) {
            roleRepository.findByName(roleName).orElseGet(() -> {
                Role role = new Role();
                role.setName(roleName);
                roleRepository.save(role);
                log.info("Role dibuat: {}", roleName);
                return role;
            });
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
