package id.go.kemenkoinfra.ipfo.sifpi.project.service.impl;

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import id.go.kemenkoinfra.ipfo.sifpi.common.enums.Sector;
import id.go.kemenkoinfra.ipfo.sifpi.common.exception.NotFoundException;
import id.go.kemenkoinfra.ipfo.sifpi.common.services.StorageService;
import id.go.kemenkoinfra.ipfo.sifpi.project.dto.CatalogueExportFileDTO;
import id.go.kemenkoinfra.ipfo.sifpi.project.dto.request.CatalogueExportRequest;
import id.go.kemenkoinfra.ipfo.sifpi.project.model.Project;
import id.go.kemenkoinfra.ipfo.sifpi.project.model.ProjectTimeline;
import id.go.kemenkoinfra.ipfo.sifpi.project.repository.ProjectRepository;
import id.go.kemenkoinfra.ipfo.sifpi.project.service.CatalogueExportService;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CatalogueExportServiceImpl implements CatalogueExportService {

    private static final Set<String> VALID_QUARTERS = Set.of("Q1", "Q2", "Q3", "Q4");
    private static final DecimalFormat MONEY_FORMAT = new DecimalFormat("#,##0.##");

    private final ProjectRepository projectRepository;
    private final StorageService storageService;
    private final TemplateEngine templateEngine;

    @Override
    @Transactional(readOnly = true)
    public CatalogueExportFileDTO exportCatalogue(CatalogueExportRequest request) {
        List<Long> normalizedProjectIds = normalizeProjectIds(request.getProjectIds());
        String normalizedQuarter = normalizeQuarter(request.getQuarter());
        validateYear(request.getYear());

        List<Project> projects = findProjects(normalizedProjectIds);
        List<CatalogueProjectView> projectViews = buildProjectViews(projects);

        String html = renderHtml(normalizedQuarter, request.getYear(), projectViews);
        byte[] pdfBytes = renderPdf(html);

        String filename = "catalogue-" + normalizedQuarter.toLowerCase(Locale.ROOT) + "-" + request.getYear() + ".pdf";
        return CatalogueExportFileDTO.builder()
                .filename(filename)
                .data(pdfBytes)
                .build();
    }

    private String renderHtml(String quarter, Integer year, List<CatalogueProjectView> projects) {
        Context context = new Context(Locale.ENGLISH);
        context.setVariable("quarter", quarter);
        context.setVariable("year", year);
        context.setVariable("projects", projects);
        String logoSrc = loadAssetAsDataUri("static/assets/ipfo-logo.png", "image/png");
        if (logoSrc == null) {
            throw new IllegalStateException("Asset logo IPFO tidak ditemukan: static/assets/ipfo-logo.png");
        }
        context.setVariable("logoSrc", logoSrc);

        return templateEngine.process("catalogue-template", context);
    }

    private byte[] renderPdf(String html) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            PdfRendererBuilder builder = new PdfRendererBuilder();
            builder.useFastMode();
            builder.withHtmlContent(html, null);
            builder.toStream(outputStream);
            builder.run();
            return outputStream.toByteArray();
        } catch (Exception e) {
            log.error("Gagal merender HTML menjadi PDF katalog.", e);
            throw new RuntimeException("Gagal menghasilkan PDF katalog.", e);
        }
    }

    private List<CatalogueProjectView> buildProjectViews(List<Project> projects) {
        List<CatalogueProjectView> result = new ArrayList<>();

        for (int index = 0; index < projects.size(); index++) {
            Project project = projects.get(index);

            result.add(CatalogueProjectView.builder()
                    .anchorId("project-" + project.getId())
                    .pageNumber(index + 4)
                    .proposedBy(fallback(project.getOwnerInstitution(), project.getName()))
                    .investmentScheme(fallback(project.getCooperationModel(), "-"))
                    .title(fallback(project.getName(), "Untitled Project"))
                    .subtitle(fallback(project.getDescription(), "-"))
                    .locationImageSrc(resolveProjectImageSource(project.getLocationImageKey()))
                    .projectStructureImageSrc(resolveProjectImageSource(project.getProjectStructureImageKey()))
                    .narrativeParagraphs(buildNarrative(project))
                    .location(fallback(project.getLocation(), "-"))
                    .sectorLabel(resolveSectorLabel(project.getSector()))
                    .scopeOfInvestment(fallback(project.getCooperationModel(), "-"))
                    .projectDetails(fallback(project.getOwnerInstitution(), "-"))
                    .lengthOfConcession(project.getConcessionPeriod() == null ? "-" : project.getConcessionPeriod() + " years")
                    .projectAssetStatus(fallback(project.getAssetReadiness(), fallback(project.getStatus(), "-")))
                    .indicativeGovernmentSupport(fallback(project.getGovernmentSupport(), "-"))
                    .additionalInfoLines(splitLines(project.getAdditionalInfo()))
                    .totalCapex(formatMoney(project.getTotalCapex()))
                    .totalOpex(formatMoney(project.getTotalOpex()))
                    .npv(formatMoney(project.getNpv()))
                    .irr(formatPercentage(project.getIrr()))
                    .revenueStream(fallback(project.getRevenueStream(), "-"))
                    .timelineItems(buildTimeline(project.getTimelines()))
                    .build());
        }

        return result;
    }

    private List<String> buildNarrative(Project project) {
        List<String> paragraphs = new ArrayList<>();

        String valueProposition = normalizeText(project.getValueProposition());
        String description = normalizeText(project.getDescription());

        if (!valueProposition.equals("-")) {
            paragraphs.add(valueProposition);
        }
        if (!description.equals("-")) {
            paragraphs.add(description);
        }

        if (paragraphs.isEmpty()) {
            paragraphs.add("-");
        }

        return paragraphs;
    }

    private List<TimelineItemView> buildTimeline(List<ProjectTimeline> timelines) {
        if (timelines == null || timelines.isEmpty()) {
            return List.of(TimelineItemView.builder().yearRange("-").phaseDescription("-").build());
        }

        return timelines.stream()
                .map(item -> TimelineItemView.builder()
                        .yearRange(fallback(item.getTimeRange(), "-"))
                        .phaseDescription(fallback(item.getPhaseDescription(), "-"))
                        .build())
                .toList();
    }

    private String resolveSectorLabel(Sector sector) {
        if (sector == null) {
            return "-";
        }
        return sector.getLabel();
    }

    private String resolveProjectImageSource(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        String normalized = value.trim();
        if (isHttpUrl(normalized)) {
            String remoteAsDataUri = loadRemoteImageAsDataUri(normalized);
            return remoteAsDataUri != null ? remoteAsDataUri : normalized;
        }

        try {
            StorageService.FileDownload file = storageService.download(normalized);
            if (file == null || file.data() == null || file.data().length == 0) {
                return null;
            }
            return bytesToDataUri(file.data(), file.contentType());
        } catch (Exception ex) {
            log.warn("Gagal memuat image dari storage untuk key {}: {}", normalized, ex.getMessage());
            try {
                String publicUrl = storageService.getPublicUrl(normalized);
                return isHttpUrl(publicUrl) ? publicUrl : null;
            } catch (Exception fallbackEx) {
                log.warn("Gagal membentuk public URL untuk key {}: {}", normalized, fallbackEx.getMessage());
                return null;
            }
        }
    }

    private String loadRemoteImageAsDataUri(String imageUrl) {
        try (InputStream inputStream = new URL(imageUrl).openStream()) {
            byte[] bytes = inputStream.readAllBytes();
            String contentType = java.net.URLConnection.guessContentTypeFromStream(new ByteArrayInputStream(bytes));
            return bytesToDataUri(bytes, contentType);
        } catch (Exception ex) {
            log.warn("Gagal download remote image {}: {}", imageUrl, ex.getMessage());
            return null;
        }
    }

    private boolean isHttpUrl(String value) {
        String v = value.toLowerCase(Locale.ROOT);
        return v.startsWith("http://") || v.startsWith("https://");
    }

    private String bytesToDataUri(byte[] bytes, String contentType) {
        String resolvedContentType = (contentType == null || contentType.isBlank()) ? "image/png" : contentType;
        return "data:" + resolvedContentType + ";base64," + Base64.getEncoder().encodeToString(bytes);
    }

    private String loadAssetAsDataUri(String classpathPath, String defaultContentType) {
        ClassPathResource resource = new ClassPathResource(classpathPath);
        if (!resource.exists()) {
            return null;
        }

        try (InputStream inputStream = resource.getInputStream()) {
            byte[] bytes = inputStream.readAllBytes();
            return "data:" + defaultContentType + ";base64," + Base64.getEncoder().encodeToString(bytes);
        } catch (IOException ex) {
            log.warn("Gagal memuat asset {} untuk catalogue: {}", classpathPath, ex.getMessage());
            return null;
        }
    }

    private String formatMoney(BigDecimal value) {
        if (value == null) {
            return "-";
        }
        return "USD " + MONEY_FORMAT.format(value);
    }

    private String formatPercentage(BigDecimal value) {
        if (value == null) {
            return "-";
        }
        return value.stripTrailingZeros().toPlainString() + "%";
    }

    private List<String> splitLines(String text) {
        String normalized = normalizeText(text);
        if ("-".equals(normalized)) {
            return List.of("-");
        }

        String[] byNewLine = normalized.split("\\n");
        List<String> lines = new ArrayList<>();
        for (String line : byNewLine) {
            String cleaned = line.trim();
            if (!cleaned.isBlank()) {
                lines.add(cleaned);
            }
        }

        if (lines.isEmpty()) {
            return List.of(normalized);
        }

        return lines;
    }

    private String fallback(Object value, String fallback) {
        if (value == null) {
            return fallback;
        }
        String normalized = normalizeText(value.toString());
        return "-".equals(normalized) ? fallback : normalized;
    }

    private String normalizeText(String value) {
        if (value == null || value.isBlank()) {
            return "-";
        }
        return value
                .replace("\r", "\n")
                .replace("\t", " ")
                .replaceAll("[ ]{2,}", " ")
                .trim();
    }

    private List<Long> normalizeProjectIds(List<Long> projectIds) {
        if (projectIds == null || projectIds.isEmpty()) {
            throw new IllegalArgumentException("projectIds wajib diisi.");
        }

        List<Long> normalized = new ArrayList<>(new LinkedHashSet<>(projectIds));
        if (normalized.stream().anyMatch(id -> id == null || id <= 0)) {
            throw new IllegalArgumentException("Semua projectIds harus berupa angka positif.");
        }

        return normalized;
    }

    private String normalizeQuarter(String quarter) {
        if (quarter == null || quarter.isBlank()) {
            throw new IllegalArgumentException("quarter wajib diisi.");
        }

        String normalized = quarter.trim().toUpperCase(Locale.ROOT);
        if (!VALID_QUARTERS.contains(normalized)) {
            throw new IllegalArgumentException("quarter harus salah satu dari Q1, Q2, Q3, Q4.");
        }
        return normalized;
    }

    private void validateYear(Integer year) {
        if (year == null) {
            throw new IllegalArgumentException("year wajib diisi.");
        }
        if (year < 2000 || year > 2100) {
            throw new IllegalArgumentException("year harus berada pada rentang 2000-2100.");
        }
    }

    private List<Project> findProjects(List<Long> projectIds) {
        List<Project> fetchedProjects = projectRepository.findAllByIdIn(projectIds);
        Map<Long, Project> byId = fetchedProjects.stream()
                .collect(Collectors.toMap(Project::getId, project -> project, (left, right) -> left, LinkedHashMap::new));

        List<Long> missingIds = projectIds.stream()
                .filter(id -> !byId.containsKey(id))
                .toList();
        if (!missingIds.isEmpty()) {
            throw new NotFoundException("Project dengan ID " + missingIds + " tidak ditemukan.");
        }

        return projectIds.stream().map(byId::get).toList();
    }

    @Getter
    @Builder
    private static class CatalogueProjectView {
        private String anchorId;
        private Integer pageNumber;
        private String proposedBy;

        private String investmentScheme;
        private String title;
        private String subtitle;

        private String locationImageSrc;
        private String projectStructureImageSrc;
        private List<String> narrativeParagraphs;

        private String location;
        private String sectorLabel;
        private String scopeOfInvestment;
        private String projectDetails;
        private String lengthOfConcession;
        private String projectAssetStatus;
        private String indicativeGovernmentSupport;

        private List<String> additionalInfoLines;

        private String totalCapex;
        private String totalOpex;
        private String npv;
        private String irr;
        private String revenueStream;

        private List<TimelineItemView> timelineItems;
    }

    @Getter
    @Builder
    private static class TimelineItemView {
        private String yearRange;
        private String phaseDescription;
    }
}
