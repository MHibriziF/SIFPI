package id.go.kemenkoinfra.ipfo.sifpi.common.enums;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum Sector {
    PUBLIC_TRANSPORTATION("PUBLIC_TRANSPORTATION", "Public Transportation"),
    LAND_BASED_TRANSPORT("LAND_BASED_TRANSPORT", "Land Based Transport (Rails and Road Transport)"),
    WASTE_MANAGEMENT("WASTE_MANAGEMENT", "Waste Management"),
    TOLL_ROAD("TOLL_ROAD", "Toll Road"),
    AFFORDABLE_HOUSING_AND_TRANSIT_ORIENTED_DEVELOPMENT("AFFORDABLE_HOUSING_AND_TRANSIT_ORIENTED_DEVELOPMENT", "Affordable Housing and Transit-oriented Development"),
    HEALTH("HEALTH", "Health"),
    WATER_RESOURCE_DRINKING_WATER_AND_IRRIGATION("WATER_RESOURCE_DRINKING_WATER_AND_IRRIGATION", "Water Resource, Drinking Water, and Irrigation"),
    MARITIME("MARITIME", "Maritime"),
    OIL_GAS_AND_ENERGY("OIL_GAS_AND_ENERGY", "Oil & Gas, and Energy"),
    AVIATION("AVIATION", "Aviation"),
    DIGITAL_AND_TELECOMMUNICATIONS("DIGITAL_AND_TELECOMMUNICATIONS", "Digital & Telecommunications"),
    EDUCATION_RESEARCH_AND_DEVELOPMENT("EDUCATION_RESEARCH_AND_DEVELOPMENT", "Education, Research, and Development"),
    URBAN_ECONOMICS_INFRASTRUCTURE_FACILITIES("URBAN_ECONOMICS_INFRASTRUCTURE_FACILITIES", "Urban Economics Infrastructure Facilities");

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
        return fromValue(v) != null;
    }

    public static Sector fromValue(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        String normalized = value.trim();
        for (Sector sector : values()) {
            if (sector.value.equalsIgnoreCase(normalized)
                    || sector.label.equalsIgnoreCase(normalized)
                    || sector.name().equalsIgnoreCase(normalized)) {
                return sector;
            }
        }

        return null;
    }

    public static List<String> labels() {
        return Arrays.stream(values()).map(Sector::getLabel).collect(Collectors.toList());
    }

    public static List<String> valuesList() {
        return Arrays.stream(values()).map(Sector::getValue).collect(Collectors.toList());
    }
}
