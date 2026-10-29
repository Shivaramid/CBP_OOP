package mccgrs.exception;

/**
 * Thrown when citizen feedback data is invalid (e.g., rating outside 1-5).
 */
public class InvalidFeedbackException extends MunicipalException {
    public InvalidFeedbackException(String message) {
        super(message);
    }
}
