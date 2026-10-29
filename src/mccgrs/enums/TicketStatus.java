package mccgrs.enums;

/**
 * Status lifecycle of a municipal civic ticket.
 */
public enum TicketStatus {
    OPEN("Open"),
    ASSIGNED("Assigned"),
    ACCEPTED("Accepted"),
    IN_PROGRESS("In Progress"),
    ON_HOLD("On Hold"),
    ESCALATED("Escalated"),
    RESOLVED("Resolved"),
    CLOSED("Closed"),
    REOPENED("Reopened"),
    REJECTED("Rejected");

    private final String displayName;

    TicketStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    /**
     * Checks if the ticket is currently undergoing active work or triage.
     */
    public boolean isActive() {
        return this == OPEN || this == ASSIGNED || this == ACCEPTED 
            || this == IN_PROGRESS || this == ON_HOLD || this == ESCALATED 
            || this == REOPENED;
    }

    /**
     * Checks if the ticket is completed or terminal.
     */
    public boolean isTerminal() {
        return this == CLOSED || this == REJECTED;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
