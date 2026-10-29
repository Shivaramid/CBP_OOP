package mccgrs.enums;

/**
 * Priority levels and corresponding SLA durations in hours.
 */
public enum Priority {
    LOW("Low", 72),
    MEDIUM("Medium", 48),
    HIGH("High", 24),
    CRITICAL("Critical", 12);

    private final String displayName;
    private final int slaDurationHours;

    Priority(String displayName, int slaDurationHours) {
        this.displayName = displayName;
        this.slaDurationHours = slaDurationHours;
    }

    public String getDisplayName() {
        return displayName;
    }

    public int getSlaDurationHours() {
        return slaDurationHours;
    }

    @Override
    public String toString() {
        return displayName + " (" + slaDurationHours + " hrs SLA)";
    }
}
