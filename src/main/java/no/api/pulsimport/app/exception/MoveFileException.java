package no.api.pulsimport.app.exception;

/**
 *
 */
public class MoveFileException  extends RuntimeException {
    public MoveFileException(String message, Throwable cause) {
        super(message, cause);
    }

    public MoveFileException(String msg) {
        super(msg);
    }
}
