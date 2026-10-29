package mccgrs.exception;

/**
 * Thrown when attempting to register an entity with an identifier or username that already exists.
 */
public class DuplicateEntityException extends MunicipalException {
    public DuplicateEntityException(String message) {
        super(message);
    }
}
