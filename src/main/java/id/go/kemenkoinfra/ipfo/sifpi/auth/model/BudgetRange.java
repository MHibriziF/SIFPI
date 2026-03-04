package id.go.kemenkoinfra.ipfo.sifpi.auth.model;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum BudgetRange {
    LESS_THAN_1("<1", "< 1 Miliar"),
    ONE_TO_5("1-5", "1-5 Miliar"),
    FIVE_TO_10("5-10", "5-10 Miliar"),
    MORE_THAN_10(">10", "> 10 Miliar");

    private final String value;
    private final String label;

    BudgetRange(String value, String label) {
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
        for (BudgetRange b : values()) {
            if (b.value.equalsIgnoreCase(v)) return true;
        }
        return false;
    }

    public static List<String> labels() {
        return Arrays.stream(values()).map(BudgetRange::getLabel).collect(Collectors.toList());
    }

    public static List<String> valuesList() {
        return Arrays.stream(values()).map(BudgetRange::getValue).collect(Collectors.toList());
    }
}
