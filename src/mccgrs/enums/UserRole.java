package mccgrs.enums;

/**
 * Supported user roles within the Municipal Civic Complaint & Grievance Redressal System.
 */
public enum UserRole {
    CITIZEN("Citizen"),
    GRO("Grievance Redressal Officer"),
    WARD_OFFICER("Ward Officer / Supervisor"),
    FIELD_WORKER("Field Worker"),
    ADMINISTRATOR("Administrator");

    private final String displayName;

    UserRole(String displayName) {
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
