package mccgrs.exception;

/**
 * Thrown when a user cannot be located by username or ID.
 */
public class UserNotFoundException extends MunicipalException {
    public UserNotFoundException(String identifier) {
        super("User not found: " + identifier);
    }
}
