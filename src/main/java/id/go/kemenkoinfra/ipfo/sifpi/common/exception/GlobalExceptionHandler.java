package id.go.kemenkoinfra.ipfo.sifpi.common.exception;

import java.util.Arrays;
import java.util.stream.Collectors;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;

import id.go.kemenkoinfra.ipfo.sifpi.common.utils.ResponseUtil;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    private final ResponseUtil responseUtil;

    public GlobalExceptionHandler(ResponseUtil responseUtil) {
        this.responseUtil = responseUtil;
    }

    // === Custom exceptions ===

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<?> handleNotFound(NotFoundException ex) {
        log.warn("Not Found: {}", ex.getMessage(), ex);
        return responseUtil.error(ex.getMessage(), HttpStatus.NOT_FOUND);
    }
    
    @ExceptionHandler({
        NoHandlerFoundException.class,
        NoResourceFoundException.class
    })
    public ResponseEntity<?> handle404(Exception ex) {
        log.warn("Endpoint not found: {}", ex.getMessage());
        return responseUtil.error(
            "Endpoint tidak ditemukan.",
            HttpStatus.NOT_FOUND
        );
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> handleBadRequest(IllegalArgumentException ex) {
        log.warn("Bad Request: {}", ex.getMessage(), ex);
        return responseUtil.error("Permintaan tidak valid: " + ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<?> handleIllegalState(IllegalStateException ex) {
        log.warn("Conflict: {}", ex.getMessage(), ex);
        return responseUtil.error("Gagal menyimpan data baru karena konflik dalam data: " + ex.getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(SecurityException.class)
    public ResponseEntity<?> handleSecurityException(SecurityException ex) {
        log.warn("Unauthorized: {}", ex.getMessage(), ex);
        return responseUtil.error("Unauthorized: " + ex.getMessage(), HttpStatus.UNAUTHORIZED);
    }


    // === Request parsing exceptions ===

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<?> handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {
        if (ex.getCause() instanceof InvalidFormatException ife) {
            String value = String.valueOf(ife.getValue());
            Class<?> targetType = ife.getTargetType();
            if (targetType != null && targetType.isEnum()) {
                String valid = Arrays.stream(targetType.getEnumConstants())
                        .map(Object::toString)
                        .collect(Collectors.joining(", "));
                return responseUtil.error(
                        "Nilai '" + value + "' tidak valid. Nilai yang diterima: " + valid,
                        HttpStatus.BAD_REQUEST);
            }
            return responseUtil.error(
                    "Format tidak valid untuk nilai '" + value + "'.",
                    HttpStatus.BAD_REQUEST);
        }
        return responseUtil.error("Format permintaan tidak dapat dibaca.", HttpStatus.BAD_REQUEST);
    }

    // === Validation-related exceptions ===

    // For @Valid violations in @RequestBody (e.g. POST with invalid DTO)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        String errorMessage = ex.getBindingResult().getFieldErrors().stream()
            .map(error -> error.getField() + ": " + error.getDefaultMessage())
            .findFirst()
            .orElse("Data yang dikirim tidak valid.");
        log.warn("Validation failed: {}", errorMessage);
        return responseUtil.error("Permintaan tidak valid: " + errorMessage, HttpStatus.BAD_REQUEST);
    }

    // For @Valid violations on query params, path vars, etc.
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<?> handleConstraintViolation(ConstraintViolationException ex) {
        String errorMessage = ex.getConstraintViolations().stream()
            .map(v -> v.getPropertyPath() + ": " + v.getMessage())
            .findFirst()
            .orElse("Permintaan tidak valid.");
        log.warn("Constraint violation: {}", errorMessage);
        return responseUtil.error("Permintaan tidak valid: " + errorMessage, HttpStatus.BAD_REQUEST);
    }

    // === Database constraint errors ===

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<?> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        log.error("Database constraint violation: {}", ex.getMessage(), ex);
        return responseUtil.error(
            "Gagal menyimpan data: terjadi pelanggaran aturan integritas database.",
            HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(org.hibernate.exception.ConstraintViolationException.class)
    public ResponseEntity<?> handleHibernateConstraintViolation(org.hibernate.exception.ConstraintViolationException ex) {
        log.error("Hibernate constraint violation: {}", ex.getConstraintName(), ex);
        return responseUtil.error(
            "Pelanggaran aturan database pada: " + ex.getConstraintName(),
            HttpStatus.BAD_REQUEST
        );
    }

    // === Catch-all fallback ===
    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleAll(Exception ex) {
        log.error("Unhandled exception occurred", ex);
        return responseUtil.error("Terjadi kesalahan pada sistem ", HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
