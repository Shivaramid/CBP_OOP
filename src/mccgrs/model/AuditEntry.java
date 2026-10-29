package mccgrs.model;

import mccgrs.util.DateTimeUtil;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Represents an immutable audit log entry capturing operational, status, or administrative changes.
 */
public class AuditEntry implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String auditId;
    private final LocalDateTime timestamp;
    private final String ticketId;
    private final String action;
    private final String previousState;
    private final String newState;
    private final String performedByUserId;
    private final String performedByRole;
    private final String details;

    public AuditEntry(String auditId, String ticketId, String action,
                      String previousState, String newState,
                      String performedByUserId, String performedByRole, String details) {
        this.auditId = auditId;
        this.timestamp = LocalDateTime.now();
        this.ticketId = ticketId;
        this.action = action;
        this.previousState = previousState;
        this.newState = newState;
        this.performedByUserId = performedByUserId;
        this.performedByRole = performedByRole;
        this.details = details;
    }

    public String getAuditId() {
        return auditId;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public String getTicketId() {
        return ticketId;
    }

    public String getAction() {
        return action;
    }

    public String getPreviousState() {
        return previousState;
    }

    public String getNewState() {
        return newState;
    }

    public String getPerformedByUserId() {
        return performedByUserId;
    }

    public String getPerformedByRole() {
        return performedByRole;
    }

    public String getDetails() {
        return details;
    }

    /**
     * Formats the audit entry in the exact human-readable format specified in the project requirements.
     */
    public String toFormattedLogString() {
        StringBuilder sb = new StringBuilder();
        sb.append(DateTimeUtil.format(timestamp)).append("\n");
        if (ticketId != null && !ticketId.isEmpty()) {
            sb.append("Ticket: ").append(ticketId).append("\n");
        }
        sb.append("Action: ").append(action).append("\n");
        if (previousState != null && newState != null) {
            sb.append("Status Changed: ").append(previousState).append(" -> ").append(newState).append("\n");
        }
        sb.append("By: ").append(performedByUserId != null ? performedByUserId : "SYSTEM");
        if (performedByRole != null) {
            sb.append(" (").append(performedByRole).append(")");
        }
        sb.append("\n");
        if (details != null && !details.isEmpty()) {
            sb.append("Details: ").append(details).append("\n");
        }
        sb.append("----------------------------------------------------------------\n");
        return sb.toString();
    }

    @Override
    public String toString() {
        return String.format("[%s] %s | Ticket: %s | By: %s | %s",
                DateTimeUtil.format(timestamp), action, ticketId != null ? ticketId : "N/A", performedByUserId, details);
    }
}
