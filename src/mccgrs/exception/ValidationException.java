package mccgrs.exception;

/**
 * Thrown when entity or form input validation fails.
 */
public class ValidationException extends MunicipalException {
    public ValidationException(String message) {
        super(message);
    }
}
