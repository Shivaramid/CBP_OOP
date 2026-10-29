package mccgrs.model;

import mccgrs.enums.UserRole;

/**
 * Concrete subclass representing a municipal Field Worker / Ward Work Team member.
 * Responsible for physical field execution: accepting tickets, starting work, holding, resolving.
 */
public class FieldWorker extends User {
    private static final long serialVersionUID = 1L;

    private String assignedWardId;
    private String assignedWardName;
    private String departmentSpecialization; // e.g., "Sanitation", "Electrical", "Road Maintenance"
    private int activeTaskCount;

    public FieldWorker(String userId, String name, String username, String passwordHash,
                       String assignedWardId, String assignedWardName, String departmentSpecialization) {
        super(userId, name, username, passwordHash, UserRole.FIELD_WORKER);
        this.assignedWardId = assignedWardId;
        this.assignedWardName = assignedWardName;
        this.departmentSpecialization = departmentSpecialization;
        this.activeTaskCount = 0;
    }

    public String getAssignedWardId() {
        return assignedWardId;
    }

    public void setAssignedWardId(String assignedWardId) {
        this.assignedWardId = assignedWardId;
    }

    public String getAssignedWardName() {
        return assignedWardName;
    }

    public void setAssignedWardName(String assignedWardName) {
        this.assignedWardName = assignedWardName;
    }

    public String getDepartmentSpecialization() {
        return departmentSpecialization;
    }

    public void setDepartmentSpecialization(String departmentSpecialization) {
        this.departmentSpecialization = departmentSpecialization;
    }

    public int getActiveTaskCount() {
        return activeTaskCount;
    }

    public void setActiveTaskCount(int activeTaskCount) {
        this.activeTaskCount = Math.max(0, activeTaskCount);
    }

    public void incrementActiveTasks() {
        this.activeTaskCount++;
    }

    public void decrementActiveTasks() {
        if (this.activeTaskCount > 0) {
            this.activeTaskCount--;
        }
    }

    @Override
    public String getDashboardTitle() {
        return String.format("FIELD WORKER WORKBENCH — %s (%s)", getName(), departmentSpecialization);
    }

    @Override
    public String getAssignmentSummary() {
        return String.format("Ward: '%s' | Specialization: %s | Active Tasks: %d",
                assignedWardName != null ? assignedWardName : "Unassigned", departmentSpecialization, activeTaskCount);
    }

    /**
     * Field worker can manage tickets assigned explicitly to them.
     */
    @Override
    public boolean canManageTicket(Ticket ticket) {
        return ticket != null && getUserId().equals(ticket.getAssignedFieldWorkerId());
    }
}
