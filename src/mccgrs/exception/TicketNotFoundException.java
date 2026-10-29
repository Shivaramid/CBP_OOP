package mccgrs.exception;

/**
 * Thrown when a specified ticket cannot be found by its identifier.
 */
public class TicketNotFoundException extends MunicipalException {
    public TicketNotFoundException(String ticketId) {
        super("Ticket not found with ID: " + ticketId);
    }
}
