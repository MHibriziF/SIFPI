package id.go.kemenkoinfra.ipfo.sifpi.project.service.impl;

import id.go.kemenkoinfra.ipfo.sifpi.common.exception.NotFoundException;
import id.go.kemenkoinfra.ipfo.sifpi.common.services.StorageService;
import id.go.kemenkoinfra.ipfo.sifpi.project.dto.CatalogueExportResponseDTO;
import id.go.kemenkoinfra.ipfo.sifpi.project.dto.request.CatalogueExportRequest;
import id.go.kemenkoinfra.ipfo.sifpi.project.model.Project;
import id.go.kemenkoinfra.ipfo.sifpi.project.model.ProjectRevision;
import id.go.kemenkoinfra.ipfo.sifpi.project.model.ProjectTimeline;
import id.go.kemenkoinfra.ipfo.sifpi.project.repository.ProjectRepository;
import id.go.kemenkoinfra.ipfo.sifpi.project.service.CatalogueExportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
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

    private static final String STORAGE_FOLDER = "catalogues";
    private static final Set<String> VALID_QUARTERS = Set.of("Q1", "Q2", "Q3", "Q4");
    private static final float PAGE_MARGIN = 50f;
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm");

    private final ProjectRepository projectRepository;
    private final StorageService storageService;

    @Override
    @Transactional(readOnly = true)
    public CatalogueExportResponseDTO exportCatalogue(CatalogueExportRequest request) {
        List<Long> normalizedProjectIds = normalizeProjectIds(request.getProjectIds());
        String normalizedQuarter = normalizeQuarter(request.getQuarter());
        validateYear(request.getYear());

        List<Project> projects = findProjects(normalizedProjectIds);
        byte[] pdfBytes = generatePdf(projects, normalizedQuarter, request.getYear());

        String filename = "catalogue-" + normalizedQuarter.toLowerCase(Locale.ROOT) + "-" + request.getYear() + ".pdf";
        MultipartFile pdfMultipart = new InMemoryMultipartFile(filename, "application/pdf", pdfBytes);
        String fileKey = storageService.upload(STORAGE_FOLDER, pdfMultipart);

        return CatalogueExportResponseDTO.builder()
                .fileKey(fileKey)
                .fileUrl(storageService.getPublicUrl(fileKey))
                .build();
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
                .collect(Collectors.toMap(Project::getId, p -> p, (left, right) -> left, LinkedHashMap::new));

        List<Long> missingIds = projectIds.stream()
                .filter(id -> !byId.containsKey(id))
                .toList();
        if (!missingIds.isEmpty()) {
            throw new NotFoundException("Project dengan ID " + missingIds + " tidak ditemukan.");
        }

        return projectIds.stream()
                .map(byId::get)
                .toList();
    }

    private byte[] generatePdf(List<Project> projects, String quarter, Integer year) {
        try (PDDocument document = new PDDocument();
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

            PDPage coverPage = newPage(document);
            drawCoverPage(document, coverPage, quarter, year);

            PDPage tocPage = newPage(document);
            PDPage introductionPage = newPage(document);
            drawIntroductionPage(document, introductionPage);

            Map<Long, Integer> pageIndexByProjectId = new LinkedHashMap<>();
            for (Project project : projects) {
                int startPageNumber = document.getNumberOfPages() + 1;
                pageIndexByProjectId.put(project.getId(), startPageNumber);
                drawProjectDetailPage(document, project);
            }

            drawTocPage(document, tocPage, projects, pageIndexByProjectId, quarter, year);
            document.save(outputStream);
            return outputStream.toByteArray();
        } catch (IOException e) {
            log.error("Gagal membuat PDF katalog proyek.", e);
            throw new RuntimeException("Gagal membuat PDF katalog proyek.", e);
        }
    }

    private PDPage newPage(PDDocument document) {
        PDPage page = new PDPage(PDRectangle.A4);
        document.addPage(page);
        return page;
    }

    private void drawCoverPage(PDDocument document, PDPage page, String quarter, Integer year) throws IOException {
        try (PDPageContentStream content = new PDPageContentStream(document, page)) {
            PDRectangle mediaBox = page.getMediaBox();
            float pageWidth = mediaBox.getWidth();
            float pageHeight = mediaBox.getHeight();

            writeCentered(content, pageWidth, pageHeight - 220, "IPFO", 40, true);
            writeCentered(content, pageWidth, pageHeight - 270, "Catalogue for Quarter " + quarter + " " + year, 18, false);
        }
    }

    private void drawTocPage(PDDocument document,
                             PDPage tocPage,
                             List<Project> projects,
                             Map<Long, Integer> pageIndexByProjectId,
                             String quarter,
                             Integer year) throws IOException {
        try (PDPageContentStream content = new PDPageContentStream(document, tocPage)) {
            float y = tocPage.getMediaBox().getHeight() - PAGE_MARGIN;

            y = writeLine(content, PAGE_MARGIN, y, "Table of Contents", 20, true);
            y = writeLine(content, PAGE_MARGIN, y - 6, "Catalogue Quarter " + quarter + " " + year, 12, false);
            y -= 18;

            for (int index = 0; index < projects.size(); index++) {
                Project project = projects.get(index);
                Integer pageNumber = pageIndexByProjectId.get(project.getId());
                String title = (index + 1) + ". " + safe(project.getName());
                String entry = title + " ................................ " + pageNumber;
                y = writeWrapped(content, PAGE_MARGIN, y, entry, 11, tocPage.getMediaBox().getWidth() - (2 * PAGE_MARGIN));
                y -= 6;
            }
        }
    }

    private void drawIntroductionPage(PDDocument document, PDPage page) throws IOException {
        try (PDPageContentStream content = new PDPageContentStream(document, page)) {
            float y = page.getMediaBox().getHeight() - PAGE_MARGIN;

            y = writeLine(content, PAGE_MARGIN, y, "Pendahuluan", 20, true);
            y -= 8;

            y = writeWrapped(content, PAGE_MARGIN, y,
                    "Katalog ini disusun oleh IPFO untuk memberikan gambaran ringkas mengenai proyek-proyek infrastruktur prioritas "
                            + "yang siap dipromosikan pada periode berjalan.", 12, page.getMediaBox().getWidth() - (2 * PAGE_MARGIN));
            y -= 8;
            y = writeWrapped(content, PAGE_MARGIN, y,
                    "Setiap profil proyek berisi informasi utama terkait sektor, lokasi, value proposition, kesiapan aset, skema kerja sama, "
                            + "indikator finansial, serta timeline pengembangan proyek.", 12, page.getMediaBox().getWidth() - (2 * PAGE_MARGIN));
            y -= 8;
            writeWrapped(content, PAGE_MARGIN, y,
                    "Dokumen ini ditujukan untuk mendukung proses kurasi, komunikasi, dan pengambilan keputusan dalam penyusunan pipeline investasi.",
                    12, page.getMediaBox().getWidth() - (2 * PAGE_MARGIN));
        }
    }

    private void drawProjectDetailPage(PDDocument document, Project project) throws IOException {
        PDPage page = newPage(document);
        try (PDPageContentStream content = new PDPageContentStream(document, page)) {
            float pageWidth = page.getMediaBox().getWidth();
            float contentWidth = pageWidth - (2 * PAGE_MARGIN);
            float y = page.getMediaBox().getHeight() - PAGE_MARGIN;

            y = writeLine(content, PAGE_MARGIN, y, safe(project.getName()), 17, true);
            y = writeLine(content, PAGE_MARGIN, y, "Project ID: " + project.getId(), 10, false);
            y -= 8;

            y = writeField(content, PAGE_MARGIN, y, contentWidth, "Owner ID", safe(project.getOwnerId()));
            y = writeField(content, PAGE_MARGIN, y, contentWidth, "Status", safe(project.getStatus()));
            y = writeField(content, PAGE_MARGIN, y, contentWidth, "Sector", safe(project.getSector()));
            y = writeField(content, PAGE_MARGIN, y, contentWidth, "Description", safe(project.getDescription()));
            y = writeField(content, PAGE_MARGIN, y, contentWidth, "Location", safe(project.getLocation()));
            y = writeField(content, PAGE_MARGIN, y, contentWidth, "Value Proposition", safe(project.getValueProposition()));
            y = writeField(content, PAGE_MARGIN, y, contentWidth, "Owner Institution", safe(project.getOwnerInstitution()));
            y = writeField(content, PAGE_MARGIN, y, contentWidth, "Contact Person", safe(project.getContactPersonName()));
            y = writeField(content, PAGE_MARGIN, y, contentWidth, "Contact Email", safe(project.getContactPersonEmail()));
            y = writeField(content, PAGE_MARGIN, y, contentWidth, "Contact Phone", safe(project.getContactPersonPhone()));
            y = writeField(content, PAGE_MARGIN, y, contentWidth, "Cooperation Model", safe(project.getCooperationModel()));
            y = writeField(content, PAGE_MARGIN, y, contentWidth, "Concession Period", valueOf(project.getConcessionPeriod()));
            y = writeField(content, PAGE_MARGIN, y, contentWidth, "Asset Readiness", safe(project.getAssetReadiness()));
            y = writeField(content, PAGE_MARGIN, y, contentWidth, "Government Support", safe(project.getGovernmentSupport()));
            y = writeField(content, PAGE_MARGIN, y, contentWidth, "Total CAPEX", valueOf(project.getTotalCapex()));
            y = writeField(content, PAGE_MARGIN, y, contentWidth, "Total OPEX", valueOf(project.getTotalOpex()));
            y = writeField(content, PAGE_MARGIN, y, contentWidth, "NPV", valueOf(project.getNpv()));
            y = writeField(content, PAGE_MARGIN, y, contentWidth, "IRR", valueOf(project.getIrr()));
            y = writeField(content, PAGE_MARGIN, y, contentWidth, "Revenue Stream", safe(project.getRevenueStream()));
            y = writeField(content, PAGE_MARGIN, y, contentWidth, "Feasibility Study", valueOf(project.getIsFeasibilityStudy()));
            y = writeField(content, PAGE_MARGIN, y, contentWidth, "Additional Info", safe(project.getAdditionalInfo()));
            y = writeField(content, PAGE_MARGIN, y, contentWidth, "Location Image URL", safe(resolvePublicUrl(project.getLocationImageKey())));
            y = writeField(content, PAGE_MARGIN, y, contentWidth, "Project Structure Image URL", safe(resolvePublicUrl(project.getProjectStructureImageKey())));
            y = writeField(content, PAGE_MARGIN, y, contentWidth, "Project File URL", safe(resolvePublicUrl(project.getProjectFileKey())));
            y = writeField(content, PAGE_MARGIN, y, contentWidth, "Is Submitted", valueOf(project.getIsSubmitted()));
            y = writeField(content, PAGE_MARGIN, y, contentWidth, "Created At", formatDateTime(project.getCreatedAt()));
            y = writeField(content, PAGE_MARGIN, y, contentWidth, "Edited At", formatDateTime(project.getEditedAt()));

            y = writeField(content, PAGE_MARGIN, y, contentWidth, "Timelines", buildTimelinesText(project.getTimelines()));
            writeField(content, PAGE_MARGIN, y, contentWidth, "Revisions", buildRevisionsText(project.getRevisions()));
        }
    }

    private String buildTimelinesText(List<ProjectTimeline> timelines) {
        if (timelines == null || timelines.isEmpty()) {
            return "-";
        }
        return timelines.stream()
                .map(item -> "[" + safe(item.getTimeRange()) + "] " + safe(item.getPhaseDescription()))
                .collect(Collectors.joining("; "));
    }

    private String buildRevisionsText(List<ProjectRevision> revisions) {
        if (revisions == null || revisions.isEmpty()) {
            return "-";
        }
        return revisions.stream()
                .map(item -> safe(item.getCreatorName()) + " (" + formatDateTime(item.getCreatedAt()) + "): " + safe(item.getRevisionText()))
                .collect(Collectors.joining("; "));
    }

    private float writeField(PDPageContentStream content,
                             float x,
                             float y,
                             float width,
                             String label,
                             String value) throws IOException {
        y = writeLine(content, x, y, label + ":", 11, true);
        y = writeWrapped(content, x + 12, y, value, 10, width - 12);
        return y - 4;
    }

    private float writeLine(PDPageContentStream content, float x, float y, String text, int size, boolean bold) throws IOException {
        content.beginText();
        content.setFont(font(bold), size);
        content.newLineAtOffset(x, y);
        content.showText(sanitize(text));
        content.endText();
        return y - (size + 3);
    }

    private float writeWrapped(PDPageContentStream content,
                               float x,
                               float y,
                               String text,
                               int size,
                               float maxWidth) throws IOException {
        List<String> lines = wrapLines(sanitize(text), size, maxWidth);
        for (String line : lines) {
            y = writeLine(content, x, y, line, size, false);
        }
        return y;
    }

    private void writeCentered(PDPageContentStream content, float pageWidth, float y, String text, int size, boolean bold) throws IOException {
        float textWidth = font(bold).getStringWidth(sanitize(text)) / 1000 * size;
        float x = (pageWidth - textWidth) / 2;
        writeLine(content, x, y, text, size, bold);
    }

    private List<String> wrapLines(String text, int fontSize, float maxWidth) throws IOException {
        List<String> lines = new ArrayList<>();
        if (text == null || text.isBlank()) {
            lines.add("-");
            return lines;
        }

        String[] paragraphs = text.split("\\n");
        for (String paragraph : paragraphs) {
            String[] words = paragraph.split("\\s+");
            StringBuilder line = new StringBuilder();
            for (String word : words) {
                String candidate = line.isEmpty() ? word : line + " " + word;
                float width = font(false).getStringWidth(candidate) / 1000 * fontSize;
                if (width > maxWidth && !line.isEmpty()) {
                    lines.add(line.toString());
                    line = new StringBuilder(word);
                } else {
                    line = new StringBuilder(candidate);
                }
            }
            if (!line.isEmpty()) {
                lines.add(line.toString());
            }
        }
        return lines;
    }

    private PDType1Font font(boolean bold) {
        return new PDType1Font(bold ? Standard14Fonts.FontName.HELVETICA_BOLD : Standard14Fonts.FontName.HELVETICA);
    }

    private String resolvePublicUrl(String key) {
        if (key == null || key.isBlank()) {
            return "-";
        }
        return storageService.getPublicUrl(key);
    }

    private String sanitize(String value) {
        return value == null ? "-" : value.replace("\r", " ").replace("\t", " ");
    }

    private String safe(Object value) {
        return value == null ? "-" : value.toString();
    }

    private String valueOf(Object value) {
        return value == null ? "-" : String.valueOf(value);
    }

    private String formatDateTime(java.time.LocalDateTime dateTime) {
        if (dateTime == null) {
            return "-";
        }
        return dateTime.format(DATE_TIME_FORMATTER);
    }

    @RequiredArgsConstructor
    private static class InMemoryMultipartFile implements MultipartFile {
        private final String originalFilename;
        private final String contentType;
        private final byte[] content;

        @Override
        public String getName() {
            return originalFilename;
        }

        @Override
        public String getOriginalFilename() {
            return originalFilename;
        }

        @Override
        public String getContentType() {
            return contentType;
        }

        @Override
        public boolean isEmpty() {
            return content == null || content.length == 0;
        }

        @Override
        public long getSize() {
            return content == null ? 0 : content.length;
        }

        @Override
        public byte[] getBytes() {
            return content;
        }

        @Override
        public InputStream getInputStream() {
            return new java.io.ByteArrayInputStream(content);
        }

        @Override
        public void transferTo(java.io.File dest) throws IOException, IllegalStateException {
            java.nio.file.Files.write(dest.toPath(), content);
        }
    }
}
