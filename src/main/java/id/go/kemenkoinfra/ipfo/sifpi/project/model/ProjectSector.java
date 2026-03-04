package id.go.kemenkoinfra.ipfo.sifpi.project.model;

public enum ProjectSector {
    PUBLIC_TRANSPORTATION("Public Transportation"),
    LAND_BASED_TRANSPORT("Land Based Transport (Rails and Road Transport)"),
    WASTE_MANAGEMENT("Waste Management"),
    TOLL_ROAD("Toll Road"),
    AFFORDABLE_HOUSING_AND_TRANSIT_ORIENTED_DEVELOPMENT("Affordable Housing and Transit-oriented Development"),
    HEALTH("Health"),
    WATER_RESOURCE_DRINKING_WATER_AND_IRRIGATION("Water Resource, Drinking Water, and Irrigation"),
    MARITIME("Maritime"),
    OIL_GAS_AND_ENERGY("Oil & Gas, and Energy"),
    AVIATION("Aviation"),
    DIGITAL_AND_TELECOMMUNICATIONS("Digital & Telecommunications"),
    EDUCATION_RESEARCH_AND_DEVELOPMENT("Education, Research, and Development"),
    URBAN_ECONOMICS_INFRASTRUCTURE_FACILITIES("Urban Economics Infrastructure Facilities");

    private final String label;

    ProjectSector(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public static ProjectSector fromValue(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        String normalized = value.trim();
        for (ProjectSector sector : values()) {
            if (sector.name().equalsIgnoreCase(normalized) || sector.label.equalsIgnoreCase(normalized)) {
                return sector;
            }
        }
        throw new IllegalArgumentException("Unknown project sector: " + value);
    }
}
