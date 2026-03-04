package id.go.kemenkoinfra.ipfo.sifpi.auth.model;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum Sector {
    AIR_BERSIH("AIR_BERSIH", "Air Bersih"),
    TRANSPORTASI("TRANSPORTASI", "Transportasi"),
    ENERGI("ENERGI", "Energi"),
    SAMPAH("SAMPAH", "Sampah"),
    PERUMAHAN("PERUMAHAN", "Perumahan"),
    MBGI("MBGI", "MBGI");

    private final String value;
    private final String label;

    Sector(String value, String label) {
        this.value = value;
        this.label = label;
    }

    public String getValue() {
        return value;
    }

    public String getLabel() {
        return label;
    }

    public static boolean isValidValue(String v) {
        if (v == null) return false;
        for (Sector s : values()) {
            if (s.value.equalsIgnoreCase(v) || s.label.equalsIgnoreCase(v)) return true;
        }
        return false;
    }

    public static List<String> labels() {
        return Arrays.stream(values()).map(Sector::getLabel).collect(Collectors.toList());
    }

    public static List<String> valuesList() {
        return Arrays.stream(values()).map(Sector::getValue).collect(Collectors.toList());
    }
}
