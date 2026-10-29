package mccgrs.enums;

/**
 * Standard municipal complaint categories.
 */
public enum ComplaintCategory {
    ROAD("Road & Potholes"),
    GARBAGE("Garbage & Solid Waste"),
    WATER("Water Supply & Leakage"),
    STREET_LIGHT("Street Lighting"),
    DRAINAGE("Drainage & Sewage Overflow"),
    SANITATION("Public Sanitation & Cleanliness"),
    PARK("Parks & Playgrounds"),
    OTHER("Other Municipal Civic Issues");

    private final String displayName;

    ComplaintCategory(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
