package id.go.kemenkoinfra.ipfo.sifpi.project.model;

public enum ProjectStatus {
    DRAFT("dibuat"),
    DIAJUKAN("diajukan"),
    IN_REVIEW("in review"),
    PERBAIKAN_DATA("perbaikan data"),
    TERVERIFIKASI("terverifikasi"),
    TERPUBLIKASI("terpublikasi");

    private final String label;

    ProjectStatus(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
