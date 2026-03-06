package id.go.kemenkoinfra.ipfo.sifpi.common.exception;

import java.util.List;

import id.go.kemenkoinfra.ipfo.sifpi.usermanagement.dto.BulkInsertRowErrorDTO;
import lombok.Getter;

@Getter
public class BulkInsertValidationException extends RuntimeException {

    private final List<BulkInsertRowErrorDTO> errors;

    public BulkInsertValidationException(String message, List<BulkInsertRowErrorDTO> errors) {
        super(message);
        this.errors = errors;
    }
}
