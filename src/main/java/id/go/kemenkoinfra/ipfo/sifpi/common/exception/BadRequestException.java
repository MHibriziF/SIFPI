package id.go.kemenkoinfra.ipfo.sifpi.common.exception;

/**
 * Exception thrown when a client request is invalid or malformed.
 * Returns HTTP 400 Bad Request.
 */
public class BadRequestException extends RuntimeException {
    public BadRequestException(String message) {
        super(message);
    }

    public BadRequestException(String message, Throwable cause) {
        super(message, cause);
    }
}
