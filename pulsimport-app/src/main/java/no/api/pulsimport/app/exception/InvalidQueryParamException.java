package no.api.pulsimport.app.exception;

/**
 * Raised when invalid query parameter supplied (in @Controller)
 *
 * @see no.api.puls.webapp.controller.GlobalExceptionControllerHandling
 */
public class InvalidQueryParamException extends RuntimeException {

    public InvalidQueryParamException() {
        super();
    }

    public InvalidQueryParamException(Throwable cause) {
        super(cause);
    }

    public InvalidQueryParamException(String message) {
        super(message);
    }

    public InvalidQueryParamException(String message, Throwable cause) {
        super(message, cause);
    }
}
