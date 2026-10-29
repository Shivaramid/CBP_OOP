package mccgrs.model;

import mccgrs.enums.UserRole;

/**
 * Concrete subclass representing a Central Municipal System Administrator.
 * Holds authority over user provisioning, location management, system configuration, and audit logs.
 */
public class Administrator extends User {
    private static final long serialVersionUID = 1L;

    private String adminLevel;
    private String department;

    public Administrator(String userId, String name, String username, String passwordHash,
                         String adminLevel, String department) {
        super(userId, name, username, passwordHash, UserRole.ADMINISTRATOR);
        this.adminLevel = adminLevel;
        this.department = department;
    }

    public String getAdminLevel() {
        return adminLevel;
    }

    public void setAdminLevel(String adminLevel) {
        this.adminLevel = adminLevel;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    @Override
    public String getDashboardTitle() {
        return "CENTRAL MUNICIPAL ADMINISTRATIVE COMMAND — " + department;
    }

    @Override
    public String getAssignmentSummary() {
        return String.format("Admin Level: %s | Department: %s | System-Wide Jurisdiction", adminLevel, department);
    }

    /**
     * Central administrators have oversight across all tickets.
     */
    @Override
    public boolean canManageTicket(Ticket ticket) {
        return ticket != null;
    }
}
