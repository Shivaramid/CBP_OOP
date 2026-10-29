package mccgrs.model;

import mccgrs.exception.InvalidFeedbackException;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Encapsulates citizen feedback upon ticket resolution or closure.
 */
public class Feedback implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String feedbackId;
    private final String ticketId;
    private final String citizenId;
    private final int rating; // 1 to 5
    private final String comment;
    private final LocalDateTime submittedAt;

    public Feedback(String feedbackId, String ticketId, String citizenId, int rating, String comment) {
        if (rating < 1 || rating > 5) {
            throw new InvalidFeedbackException("Feedback rating must be an integer between 1 and 5. Given: " + rating);
        }
        this.feedbackId = Objects.requireNonNull(feedbackId, "feedbackId cannot be null");
        this.ticketId = Objects.requireNonNull(ticketId, "ticketId cannot be null");
        this.citizenId = Objects.requireNonNull(citizenId, "citizenId cannot be null");
        this.rating = rating;
        this.comment = comment != null ? comment.trim() : "";
        this.submittedAt = LocalDateTime.now();
    }

    public String getFeedbackId() {
        return feedbackId;
    }

    public String getTicketId() {
        return ticketId;
    }

    public String getCitizenId() {
        return citizenId;
    }

    public int getRating() {
        return rating;
    }

    public String getComment() {
        return comment;
    }

    public LocalDateTime getSubmittedAt() {
        return submittedAt;
    }

    @Override
    public String toString() {
        return String.format("[%d/5 Stars] %s (Ticket: %s, Citizen: %s)",
                rating, comment.isEmpty() ? "(No comment)" : "\"" + comment + "\"", ticketId, citizenId);
    }
}
