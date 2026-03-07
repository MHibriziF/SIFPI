package id.go.kemenkoinfra.ipfo.sifpi.common.exception;

/**
 * Exception thrown when user doesn't have permission to access a resource
 */
public class ForbiddenException extends RuntimeException {
    
    public ForbiddenException(String message) {
        super(message);
    }
    
    public ForbiddenException(String message, Throwable cause) {
        super(message, cause);
    }
}
