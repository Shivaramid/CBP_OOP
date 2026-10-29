package mccgrs.exception;

import mccgrs.enums.TicketStatus;

/**
 * Thrown when attempting an illegal or prohibited ticket lifecycle state transition.
 */
public class InvalidStatusTransitionException extends MunicipalException {
    public InvalidStatusTransitionException(TicketStatus from, TicketStatus to, String reason) {
        super(String.format("Invalid ticket transition from '%s' to '%s': %s", from, to, reason));
    }

    public InvalidStatusTransitionException(String message) {
        super(message);
    }
}
