package id.go.kemenkoinfra.ipfo.sifpi.common.exception;

public class ConflictException extends RuntimeException {

    public ConflictException(String message) {
        super(message);
    }

    public ConflictException(String entity, String field, String value) {
        super(String.format("%s dengan %s '%s' sudah terdaftar.", entity, field, value));
    }
}
