package id.go.kemenkoinfra.ipfo.sifpi.project.service.impl;

import java.math.BigDecimal;
import java.net.URI;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import id.go.kemenkoinfra.ipfo.sifpi.auth.model.User;
import id.go.kemenkoinfra.ipfo.sifpi.auth.repository.UserRepository;
import id.go.kemenkoinfra.ipfo.sifpi.common.enums.ProjectStatus;
import id.go.kemenkoinfra.ipfo.sifpi.common.enums.Sector;
import id.go.kemenkoinfra.ipfo.sifpi.common.services.StorageService;
import id.go.kemenkoinfra.ipfo.sifpi.project.dto.BulkInsertProjectResultDTO;
import id.go.kemenkoinfra.ipfo.sifpi.project.dto.BulkInsertProjectRowErrorDTO;
import id.go.kemenkoinfra.ipfo.sifpi.project.dto.ProjectResponseDTO;
import id.go.kemenkoinfra.ipfo.sifpi.project.dto.request.BulkInsertProjectRequest;
import id.go.kemenkoinfra.ipfo.sifpi.project.dto.request.CreateProjectRequest;
import id.go.kemenkoinfra.ipfo.sifpi.project.mapper.ProjectMapper;
import id.go.kemenkoinfra.ipfo.sifpi.project.model.Project;
import id.go.kemenkoinfra.ipfo.sifpi.project.model.ProjectTimeline;
import id.go.kemenkoinfra.ipfo.sifpi.project.repository.ProjectRepository;
import id.go.kemenkoinfra.ipfo.sifpi.project.service.CreateProjectService;
import jakarta.mail.internet.AddressException;
import jakarta.mail.internet.InternetAddress;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class CreateProjectServiceImpl implements CreateProjectService {

    private static final String STORAGE_FOLDER = "projects";
    private static final int MAX_BULK_ROWS = 500;

    private final ProjectRepository projectRepository;
    private final ProjectMapper projectMapper;
    private final StorageService storageService;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public ProjectResponseDTO createProject(
            CreateProjectRequest request,
            MultipartFile mapFile,
            MultipartFile projectStructureFile,
            MultipartFile projectFile) {
        User creator = resolveCurrentUser();
        validateFinancialMetrics(request);

        String locationImageKey = storageService.upload(STORAGE_FOLDER, mapFile);
        String projectStructureImageKey = storageService.upload(STORAGE_FOLDER, projectStructureFile);
        String projectFileKey = storageService.upload(STORAGE_FOLDER, projectFile);

        Project project = projectMapper.toEntity(request);
        project.setLocationImageKey(locationImageKey);
        project.setProjectStructureImageKey(projectStructureImageKey);
        project.setProjectFileKey(projectFileKey);
        project.setOwnerId(creator.getId());

        attachProjectToTimelines(project);

        Project savedProject = projectRepository.save(project);
        return projectMapper.toDTO(savedProject, storageService);
    }

    @Override
    @Transactional
    public BulkInsertProjectResultDTO bulkInsertProjects(List<BulkInsertProjectRequest> requests) {
        if (requests == null || requests.isEmpty()) {
            throw new IllegalArgumentException("Data proyek untuk import tidak boleh kosong.");
        }

        if (requests.size() > MAX_BULK_ROWS) {
            throw new IllegalArgumentException("Maksimum " + MAX_BULK_ROWS + " baris per upload.");
        }

        Map<Integer, LinkedHashSet<String>> validationErrors = new LinkedHashMap<>();
        Map<String, List<Integer>> ownerEmailRowsMap = new LinkedHashMap<>();
        Set<String> ownerEmails = new LinkedHashSet<>();

        for (int i = 0; i < requests.size(); i++) {
            int row = i + 1;
            BulkInsertProjectRequest request = requests.get(i);

            if (request == null) {
                addError(validationErrors, row, "Data baris tidak boleh null.");
                continue;
            }

            validateRequiredFields(validationErrors, row, request);
            validateContactFields(validationErrors, row, request);
            validateSector(validationErrors, row, request);
            validateFinancialFields(validationErrors, row, request);
            validateConcessionPeriod(validationErrors, row, request);
            validateFileUrls(validationErrors, row, request);

            String ownerEmail = normalizeEmail(request.getOwnerEmail());
            if (!StringUtils.hasText(ownerEmail)) {
                addError(validationErrors, row, "Email pemilik proyek wajib diisi.");
            } else {
                if (!isValidEmail(ownerEmail)) {
                    addError(validationErrors, row, "Format email pemilik proyek tidak valid.");
                }
                ownerEmailRowsMap.computeIfAbsent(ownerEmail, ignored -> new ArrayList<>()).add(row);
                ownerEmails.add(ownerEmail);
            }
        }

        Map<String, UUID> ownerIdByEmail = resolveOwnerEmails(ownerEmails, ownerEmailRowsMap, validationErrors);

        List<Project> projectsToSave = new ArrayList<>();

        for (int i = 0; i < requests.size(); i++) {
            int row = i + 1;

            if (validationErrors.containsKey(row)) {
                continue;
            }

            BulkInsertProjectRequest request = requests.get(i);
            String ownerEmail = normalizeEmail(request.getOwnerEmail());
            UUID ownerId = ownerIdByEmail.get(ownerEmail);

            try {
                String locationImageKey = storageService.uploadFromUrl(STORAGE_FOLDER, request.getLocationImageUrl().trim());
                String projectStructureImageKey = storageService.uploadFromUrl(STORAGE_FOLDER, request.getProjectStructureImageUrl().trim());
                String projectFileKey = storageService.uploadFromUrl(STORAGE_FOLDER, request.getProjectFileUrl().trim());

                Project project = buildProjectFromRequest(request, ownerId, locationImageKey, projectStructureImageKey, projectFileKey);
                projectsToSave.add(project);
            } catch (Exception e) {
                addError(validationErrors, row, "Gagal mengunduh file: " + e.getMessage());
            }
        }

        if (!projectsToSave.isEmpty()) {
            projectRepository.saveAllAndFlush(projectsToSave);
            log.info("Bulk insert proyek berhasil: {} proyek disimpan", projectsToSave.size());
        }

        List<BulkInsertProjectRowErrorDTO> errors = toRowErrorList(validationErrors);
        int successCount = projectsToSave.size();
        int failedCount = validationErrors.size();

        return new BulkInsertProjectResultDTO(successCount, failedCount, errors);
    }

    private void validateRequiredFields(Map<Integer, LinkedHashSet<String>> errors, int row,
            BulkInsertProjectRequest request) {
        if (!StringUtils.hasText(normalize(request.getName()))) {
            addError(errors, row, "Nama proyek wajib diisi.");
        } else if (request.getName().trim().length() > 200) {
            addError(errors, row, "Nama proyek maksimal 200 karakter.");
        }

        if (!StringUtils.hasText(normalize(request.getDescription()))) {
            addError(errors, row, "Deskripsi singkat wajib diisi.");
        } else if (request.getDescription().trim().length() > 300) {
            addError(errors, row, "Deskripsi singkat maksimal 300 karakter.");
        }

        if (!StringUtils.hasText(normalize(request.getLocation()))) {
            addError(errors, row, "Lokasi proyek wajib diisi.");
        }

        if (!StringUtils.hasText(normalize(request.getValueProposition()))) {
            addError(errors, row, "Value Proposition wajib diisi.");
        }

        if (!StringUtils.hasText(normalize(request.getOwnerInstitution()))) {
            addError(errors, row, "Instansi pemilik wajib diisi.");
        }

        if (!StringUtils.hasText(normalize(request.getCooperationModel()))) {
            addError(errors, row, "Model kerja sama wajib diisi.");
        }

        if (!StringUtils.hasText(normalize(request.getAssetReadiness()))) {
            addError(errors, row, "Kesiapan aset wajib diisi.");
        }

        if (!StringUtils.hasText(normalize(request.getGovernmentSupport()))) {
            addError(errors, row, "Bantuan pemerintah wajib diisi.");
        }

        if (!StringUtils.hasText(normalize(request.getRevenueStream()))) {
            addError(errors, row, "Revenue stream wajib diisi.");
        }

        if (request.getIsFeasibilityStudy() == null) {
            addError(errors, row, "Status feasibility study wajib diisi.");
        }
    }

    private void validateContactFields(Map<Integer, LinkedHashSet<String>> errors, int row,
            BulkInsertProjectRequest request) {
        if (!StringUtils.hasText(normalize(request.getContactPersonName()))) {
            addError(errors, row, "Nama narahubung wajib diisi.");
        }

        String contactEmail = normalize(request.getContactPersonEmail());
        if (!StringUtils.hasText(contactEmail)) {
            addError(errors, row, "Email narahubung wajib diisi.");
        } else if (!isValidEmail(contactEmail)) {
            addError(errors, row, "Format email narahubung tidak valid.");
        }

        String contactPhone = normalize(request.getContactPersonPhone());
        if (!StringUtils.hasText(contactPhone)) {
            addError(errors, row, "Nomor telepon narahubung wajib diisi.");
        } else if (!contactPhone.matches("^[0-9+\\- ]+$")) {
            addError(errors, row, "Format nomor telepon narahubung tidak valid.");
        }
    }

    private void validateSector(Map<Integer, LinkedHashSet<String>> errors, int row,
            BulkInsertProjectRequest request) {
        String sectorValue = normalize(request.getSector());
        if (!StringUtils.hasText(sectorValue)) {
            addError(errors, row, "Sektor proyek wajib diisi.");
        } else if (Sector.fromValue(sectorValue) == null) {
            addError(errors, row, "Sektor proyek '" + sectorValue + "' tidak valid.");
        }
    }

    private void validateFinancialFields(Map<Integer, LinkedHashSet<String>> errors, int row,
            BulkInsertProjectRequest request) {
        if (request.getTotalCapex() != null && request.getTotalCapex().compareTo(BigDecimal.ZERO) < 0) {
            addError(errors, row, "Total capex tidak boleh negatif.");
        }
        if (request.getTotalOpex() != null && request.getTotalOpex().compareTo(BigDecimal.ZERO) < 0) {
            addError(errors, row, "Total opex tidak boleh negatif.");
        }
        if (request.getNpv() != null && request.getNpv().compareTo(BigDecimal.ZERO) < 0) {
            addError(errors, row, "NPV tidak boleh negatif.");
        }
        if (request.getIrr() != null && request.getIrr().compareTo(BigDecimal.ZERO) < 0) {
            addError(errors, row, "IRR tidak boleh negatif.");
        }

        if (Boolean.TRUE.equals(request.getIsFeasibilityStudy())) {
            if (request.getTotalCapex() == null) {
                addError(errors, row, "Total capex wajib diisi ketika feasibility study bernilai true.");
            }
            if (request.getTotalOpex() == null) {
                addError(errors, row, "Total opex wajib diisi ketika feasibility study bernilai true.");
            }
            if (request.getNpv() == null) {
                addError(errors, row, "NPV wajib diisi ketika feasibility study bernilai true.");
            }
            if (request.getIrr() == null) {
                addError(errors, row, "IRR wajib diisi ketika feasibility study bernilai true.");
            }
        }
    }

    private void validateConcessionPeriod(Map<Integer, LinkedHashSet<String>> errors, int row,
            BulkInsertProjectRequest request) {
        if (request.getConcessionPeriod() == null) {
            addError(errors, row, "Masa konsesi wajib diisi.");
        } else if (request.getConcessionPeriod() < 1) {
            addError(errors, row, "Masa konsesi minimal 1 tahun.");
        }
    }

    private void validateFileUrls(Map<Integer, LinkedHashSet<String>> errors, int row,
            BulkInsertProjectRequest request) {
        if (!StringUtils.hasText(normalize(request.getLocationImageUrl()))) {
            addError(errors, row, "URL gambar lokasi proyek wajib diisi.");
        } else if (!isValidUrl(request.getLocationImageUrl().trim())) {
            addError(errors, row, "URL gambar lokasi proyek tidak valid.");
        }

        if (!StringUtils.hasText(normalize(request.getProjectStructureImageUrl()))) {
            addError(errors, row, "URL gambar struktur proyek wajib diisi.");
        } else if (!isValidUrl(request.getProjectStructureImageUrl().trim())) {
            addError(errors, row, "URL gambar struktur proyek tidak valid.");
        }

        if (!StringUtils.hasText(normalize(request.getProjectFileUrl()))) {
            addError(errors, row, "URL dokumen proyek wajib diisi.");
        } else if (!isValidUrl(request.getProjectFileUrl().trim())) {
            addError(errors, row, "URL dokumen proyek tidak valid.");
        }
    }

    private boolean isValidUrl(String url) {
        try {
            URI uri = URI.create(url);
            String scheme = uri.getScheme();
            return ("http".equals(scheme) || "https".equals(scheme)) && uri.getHost() != null;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    private Map<String, UUID> resolveOwnerEmails(Set<String> ownerEmails,
            Map<String, List<Integer>> ownerEmailRowsMap,
            Map<Integer, LinkedHashSet<String>> validationErrors) {
        Map<String, UUID> ownerIdByEmail = new LinkedHashMap<>();

        if (ownerEmails.isEmpty()) {
            return ownerIdByEmail;
        }

        List<User> owners = userRepository.findAllByEmailInIgnoreCase(ownerEmails);
        Map<String, User> ownerByEmail = owners.stream()
                .collect(Collectors.toMap(
                        user -> user.getEmail().toLowerCase(Locale.ROOT),
                        user -> user));

        for (String email : ownerEmails) {
            User owner = ownerByEmail.get(email);
            if (owner == null) {
                List<Integer> rows = ownerEmailRowsMap.get(email);
                if (rows != null) {
                    rows.forEach(row -> addError(validationErrors, row,
                            "User dengan email '" + email + "' tidak ditemukan."));
                }
            } else {
                ownerIdByEmail.put(email, owner.getId());
            }
        }

        return ownerIdByEmail;
    }

    private Project buildProjectFromRequest(BulkInsertProjectRequest request, UUID ownerId,
            String locationImageKey, String projectStructureImageKey, String projectFileKey) {
        return Project.builder()
                .ownerId(ownerId)
                .name(normalize(request.getName()))
                .description(normalize(request.getDescription()))
                .sector(Sector.fromValue(normalize(request.getSector())))
                .status(ProjectStatus.DRAFT)
                .location(normalize(request.getLocation()))
                .valueProposition(normalize(request.getValueProposition()))
                .locationImageKey(locationImageKey)
                .ownerInstitution(normalize(request.getOwnerInstitution()))
                .contactPersonName(normalize(request.getContactPersonName()))
                .contactPersonEmail(normalize(request.getContactPersonEmail()))
                .contactPersonPhone(normalize(request.getContactPersonPhone()))
                .cooperationModel(normalize(request.getCooperationModel()))
                .concessionPeriod(request.getConcessionPeriod())
                .assetReadiness(normalize(request.getAssetReadiness()))
                .projectStructureImageKey(projectStructureImageKey)
                .governmentSupport(normalize(request.getGovernmentSupport()))
                .totalCapex(request.getTotalCapex())
                .totalOpex(request.getTotalOpex())
                .npv(request.getNpv())
                .irr(request.getIrr())
                .revenueStream(normalize(request.getRevenueStream()))
                .projectFileKey(projectFileKey)
                .isFeasibilityStudy(request.getIsFeasibilityStudy())
                .additionalInfo(normalize(request.getAdditionalInfo()))
                .isSubmitted(false)
                .build();
    }

    private void attachProjectToTimelines(Project project) {
        List<ProjectTimeline> timelines = project.getTimelines();
        if (timelines == null || timelines.isEmpty()) {
            return;
        }

        for (ProjectTimeline timeline : timelines) {
            timeline.setProject(project);
        }
    }

    private void validateFinancialMetrics(CreateProjectRequest request) {
        if (!Boolean.TRUE.equals(request.getIsFeasibilityStudy())) {
            return;
        }

        if (request.getTotalCapex() == null) {
            throw new IllegalArgumentException("totalCapex wajib diisi ketika isFeasibilityStudy bernilai true");
        }
        if (request.getTotalOpex() == null) {
            throw new IllegalArgumentException("totalOpex wajib diisi ketika isFeasibilityStudy bernilai true");
        }
        if (request.getNpv() == null) {
            throw new IllegalArgumentException("npv wajib diisi ketika isFeasibilityStudy bernilai true");
        }
        if (request.getIrr() == null) {
            throw new IllegalArgumentException("irr wajib diisi ketika isFeasibilityStudy bernilai true");
        }
    }

    private User resolveCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AccessDeniedException("Akses ditolak.");
        }

        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AccessDeniedException("Akses ditolak."));

        if (!user.isEmailVerified()) {
            throw new AccessDeniedException("User belum terverifikasi.");
        }

        return user;
    }

    private List<BulkInsertProjectRowErrorDTO> toRowErrorList(
            Map<Integer, LinkedHashSet<String>> validationErrors) {
        return validationErrors.entrySet().stream()
                .sorted(Comparator.comparingInt(Map.Entry::getKey))
                .map(entry -> new BulkInsertProjectRowErrorDTO(entry.getKey(), List.copyOf(entry.getValue())))
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

    private boolean isValidEmail(String email) {
        try {
            InternetAddress address = new InternetAddress(email);
            address.validate();
            return true;
        } catch (AddressException ex) {
            return false;
        }
    }
}
