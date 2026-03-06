package id.go.kemenkoinfra.ipfo.sifpi.usermanagement.service.impl;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import id.go.kemenkoinfra.ipfo.sifpi.auth.model.Role;
import id.go.kemenkoinfra.ipfo.sifpi.auth.model.User;
import id.go.kemenkoinfra.ipfo.sifpi.auth.repository.RoleRepository;
import id.go.kemenkoinfra.ipfo.sifpi.auth.repository.UserRepository;
import id.go.kemenkoinfra.ipfo.sifpi.common.config.EmailProperties;
import id.go.kemenkoinfra.ipfo.sifpi.common.exception.BulkInsertValidationException;
import id.go.kemenkoinfra.ipfo.sifpi.common.exception.NotFoundException;
import id.go.kemenkoinfra.ipfo.sifpi.common.services.EmailService;
import id.go.kemenkoinfra.ipfo.sifpi.common.utils.EmailTemplateUtil;
import id.go.kemenkoinfra.ipfo.sifpi.usermanagement.dto.BulkInsertResultDTO;
import id.go.kemenkoinfra.ipfo.sifpi.usermanagement.dto.BulkInsertRowErrorDTO;
import id.go.kemenkoinfra.ipfo.sifpi.usermanagement.dto.BulkInsertUserRequest;
import id.go.kemenkoinfra.ipfo.sifpi.usermanagement.service.BulkUserImportService;
import jakarta.mail.internet.AddressException;
import jakarta.mail.internet.InternetAddress;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BulkUserImportServiceImpl implements BulkUserImportService {

    private static final String DEFAULT_INACTIVE_ROLE_NAME = "PUBLIC";
    private static final int MAX_BULK_ROWS = 500;
    private static final int PASSWORD_LENGTH = 12;
    private static final String PASSWORD_CHARSET = "ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnpqrstuvwxyz23456789!@#$%";
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final EmailTemplateUtil emailTemplateUtil;
    private final EmailProperties emailProperties;

    @Override
    @Transactional
    public BulkInsertResultDTO bulkInsertUsers(List<BulkInsertUserRequest> requests) {
        if (requests == null || requests.isEmpty()) {
            throw new IllegalArgumentException("Data user untuk import tidak boleh kosong.");
        }

        if (requests.size() > MAX_BULK_ROWS) {
            throw new IllegalArgumentException("Maksimum " + MAX_BULK_ROWS + " baris per upload.");
        }

        Map<Integer, LinkedHashSet<String>> validationErrors = new LinkedHashMap<>();
        Map<String, List<Integer>> emailRowsMap = new LinkedHashMap<>();
        Set<String> normalizedEmails = new LinkedHashSet<>();
        Map<Integer, String> rowRoleMap = new LinkedHashMap<>();
        Set<String> providedRoleNames = new LinkedHashSet<>();

        for (int i = 0; i < requests.size(); i++) {
            int row = i + 1;
            BulkInsertUserRequest request = requests.get(i);

            if (request == null) {
                addError(validationErrors, row, "Data baris tidak boleh null.");
                continue;
            }

            String email = normalizeEmail(request.getEmail());
            String name = normalize(request.getNama());
            String phone = normalize(request.getPhone());
            String roleName = normalizeRole(request.getRole());
            Boolean isActive = request.getIsActive();

            if (!StringUtils.hasText(email)) {
                addError(validationErrors, row, "Email wajib diisi.");
            } else {
                if (!isValidEmail(email)) {
                    addError(validationErrors, row, "Format email tidak valid.");
                }
                emailRowsMap.computeIfAbsent(email, ignored -> new ArrayList<>()).add(row);
                normalizedEmails.add(email);
            }

            if (!StringUtils.hasText(name)) {
                addError(validationErrors, row, "Nama wajib diisi.");
            }

            if (isActive == null) {
                addError(validationErrors, row, "is_active wajib diisi.");
            }

            if (StringUtils.hasText(phone) && !isValidPhone(phone)) {
                addError(validationErrors, row, "Nomor telepon tidak valid.");
            }

            if (StringUtils.hasText(roleName)) {
                rowRoleMap.put(row, roleName);
                providedRoleNames.add(roleName);
            }
        }

        emailRowsMap.values().stream()
                .filter(rows -> rows.size() > 1)
                .flatMap(List::stream)
                .forEach(row -> addError(validationErrors, row, "Email duplikat dalam file import."));

        if (!normalizedEmails.isEmpty()) {
            Set<String> existingEmails = new LinkedHashSet<>(
                    userRepository.findExistingEmailsIgnoreCase(normalizedEmails));
            existingEmails.forEach(existingEmail -> {
                List<Integer> rows = emailRowsMap.get(existingEmail);
                if (rows != null) {
                    rows.forEach(row -> addError(validationErrors, row, "Email sudah terdaftar pada sistem."));
                }
            });
        }

        if (!providedRoleNames.isEmpty()) {
            Set<String> existingRoleNames = roleRepository.findByNameIn(providedRoleNames).stream()
                    .map(Role::getName)
                    .collect(Collectors.toSet());

            rowRoleMap.forEach((row, roleName) -> {
                if (!existingRoleNames.contains(roleName)) {
                    addError(validationErrors, row, "Role '" + roleName + "' tidak ditemukan.");
                }
            });
        }

        if (!validationErrors.isEmpty()) {
            throw new BulkInsertValidationException("Validasi bulk insert gagal.", toRowErrorList(validationErrors));
        }

        Set<String> roleNamesToLoad = new LinkedHashSet<>(providedRoleNames);
        roleNamesToLoad.add(DEFAULT_INACTIVE_ROLE_NAME);

        Map<String, Role> rolesByName = roleRepository.findByNameIn(roleNamesToLoad).stream()
                .collect(Collectors.toMap(Role::getName, role -> role));

        Role defaultInactiveRole = rolesByName.get(DEFAULT_INACTIVE_ROLE_NAME);
        if (defaultInactiveRole == null) {
            throw new NotFoundException("Role", DEFAULT_INACTIVE_ROLE_NAME);
        }

        List<User> usersToSave = new ArrayList<>(requests.size());
        List<PendingEmail> pendingEmails = new ArrayList<>();

        for (int i = 0; i < requests.size(); i++) {
            int row = i + 1;
            BulkInsertUserRequest request = requests.get(i);

            boolean isActive = Boolean.TRUE.equals(request.getIsActive());
            String roleName = rowRoleMap.get(row);
            Role role = StringUtils.hasText(roleName) ? rolesByName.get(roleName) : defaultInactiveRole;

            if (role == null) {
                throw new NotFoundException("Role", roleName);
            }

            String generatedPassword = generateRandomPassword();
            User user = new User();
            user.setEmail(normalizeEmail(request.getEmail()));
            user.setName(normalize(request.getNama()));
            user.setOrganization(normalize(request.getOrganisasi()));
            user.setPhone(normalize(request.getPhone()));
            user.setRole(role);
            user.setActive(isActive);
            user.setEmailVerified(isActive);
            user.setPassword(passwordEncoder.encode(generatedPassword));

            usersToSave.add(user);

            if (isActive) {
                pendingEmails.add(new PendingEmail(user.getName(), user.getEmail(), generatedPassword));
            }
        }

        userRepository.saveAllAndFlush(usersToSave);

        for (PendingEmail pendingEmail : pendingEmails) {
            sendBulkImportCredentialEmail(pendingEmail);
        }

        return new BulkInsertResultDTO(usersToSave.size());
    }

    private void sendBulkImportCredentialEmail(PendingEmail pendingEmail) {
        String html = emailTemplateUtil.load("bulk-user-created", Map.of(
                "name", pendingEmail.name(),
                "email", pendingEmail.email(),
                "password", pendingEmail.password(),
                "loginUrl", emailProperties.getLoginUrl()
        ));

        emailService.sendEmail(
                pendingEmail.email(),
                "Akun Anda Telah Didaftarkan - SIFPI",
                html
        );
    }

    private List<BulkInsertRowErrorDTO> toRowErrorList(Map<Integer, LinkedHashSet<String>> validationErrors) {
        return validationErrors.entrySet().stream()
                .sorted(Comparator.comparingInt(Map.Entry::getKey))
                .map(entry -> new BulkInsertRowErrorDTO(entry.getKey(), List.copyOf(entry.getValue())))
                .toList();
    }

    private void addError(Map<Integer, LinkedHashSet<String>> validationErrors, int row, String reason) {
        validationErrors.computeIfAbsent(row, ignored -> new LinkedHashSet<>()).add(reason);
    }

    private String normalize(String value) {
        if (value == null) {
            return null;
        }
        String normalized = value.trim();
        return normalized.isEmpty() ? null : normalized;
    }

    private String normalizeEmail(String email) {
        String normalized = normalize(email);
        return normalized == null ? null : normalized.toLowerCase(Locale.ROOT);
    }

    private String normalizeRole(String role) {
        String normalized = normalize(role);
        return normalized == null ? null : normalized.toUpperCase(Locale.ROOT);
    }

    private boolean isValidEmail(String email) {
        try {
            InternetAddress address = new InternetAddress(email);
            address.validate();
            return true;
        } catch (AddressException ex) {
            return false;
        }
    }

    private boolean isValidPhone(String phone) {
        if (!phone.matches("^[0-9+()\\-\\s]+$")) {
            return false;
        }

        String onlyDigits = phone.replaceAll("\\D", "");
        return onlyDigits.length() >= 10 && onlyDigits.length() <= 20;
    }

    private String generateRandomPassword() {
        StringBuilder generated = new StringBuilder(PASSWORD_LENGTH);
        for (int i = 0; i < PASSWORD_LENGTH; i++) {
            int randomIndex = SECURE_RANDOM.nextInt(PASSWORD_CHARSET.length());
            generated.append(PASSWORD_CHARSET.charAt(randomIndex));
        }
        return generated.toString();
    }

    private record PendingEmail(String name, String email, String password) {}
}
