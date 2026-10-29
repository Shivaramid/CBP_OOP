package mccgrs.exception;

/**
 * Thrown when an illegal assignment is attempted (e.g., assigning a worker outside their ward).
 */
public class AssignmentException extends MunicipalException {
    public AssignmentException(String message) {
        super(message);
    }
}
