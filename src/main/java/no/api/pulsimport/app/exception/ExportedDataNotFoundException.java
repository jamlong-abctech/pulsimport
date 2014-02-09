package no.api.pulsimport.app.exception;

/**
 *
 */
public class ExportedDataNotFoundException extends RuntimeException {
    public ExportedDataNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public ExportedDataNotFoundException(String msg) {
        super(msg);
    }
}
