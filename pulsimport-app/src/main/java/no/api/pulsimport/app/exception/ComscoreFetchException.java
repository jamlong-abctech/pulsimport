package no.api.pulsimport.app.exception;

/**
 * Thrown when cannot fetch data from Comscore
 */
public class ComscoreFetchException extends Exception {

    public ComscoreFetchException() {
        super();
    }

    public ComscoreFetchException(Throwable cause) {
        super(cause);
    }

    public ComscoreFetchException(String message) {
        super(message);
    }

    public ComscoreFetchException(String message, Throwable cause) {
        super(message, cause);
    }
}
