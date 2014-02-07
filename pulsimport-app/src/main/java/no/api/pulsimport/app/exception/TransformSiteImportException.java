package no.api.pulsimport.app.exception;

/**
 *
 */
public class TransformSiteImportException extends  RuntimeException{
    public TransformSiteImportException() {
        super();
    }

    public TransformSiteImportException(String message) {
        super(message);
    }

    public TransformSiteImportException(String message, Throwable cause) {
        super(message, cause);
    }

    public TransformSiteImportException(Throwable cause) {
        super(cause);
    }
}
