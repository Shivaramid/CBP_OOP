package mccgrs.exception;

/**
 * Thrown when persistence operations encounter file I/O or serialization failures.
 */
public class PersistenceException extends MunicipalException {
    public PersistenceException(String message, Throwable cause) {
        super(message, cause);
    }

    public PersistenceException(String message) {
        super(message);
    }
}
