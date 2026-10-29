package mccgrs.exception;

/**
 * Thrown when an invalid location entity (Zone, Circle, Ward, Area, Street) is supplied.
 */
public class InvalidLocationException extends MunicipalException {
    public InvalidLocationException(String message) {
        super(message);
    }
}
