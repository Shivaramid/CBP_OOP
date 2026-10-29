package mccgrs.exception;

/**
 * Base unchecked exception for all domain and operational errors in the system.
 */
public class MunicipalException extends RuntimeException {
    public MunicipalException(String message) {
        super(message);
    }

    public MunicipalException(String message, Throwable cause) {
        super(message, cause);
    }
}
