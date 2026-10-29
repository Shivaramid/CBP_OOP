package mccgrs.exception;

/**
 * Thrown when credentials fail verification, or user account is inactive or unauthorized.
 */
public class AuthenticationException extends MunicipalException {
    public AuthenticationException(String message) {
        super(message);
    }
}
