package id.go.kemenkoinfra.ipfo.sifpi.common.exception;

import java.util.UUID;


public class NotFoundException extends RuntimeException {
    public NotFoundException(String message) {
        super(message);
    }

    public NotFoundException(String entity, String identifier) {
        super(String.format("%s dengan ID '%s' tidak ditemukan.", entity, identifier));
    }

    public NotFoundException(String entity, UUID identifier) {
        super(String.format("%s dengan ID '%s' tidak ditemukan.", entity, identifier));
    }
}

