package id.go.kemenkoinfra.ipfo.sifpi.usermanagement.service;

import id.go.kemenkoinfra.ipfo.sifpi.common.exception.NotFoundException;
import id.go.kemenkoinfra.ipfo.sifpi.usermanagement.dto.request.SetPasswordRequest;
import id.go.kemenkoinfra.ipfo.sifpi.usermanagement.dto.request.UpdatePasswordRequest;

/**
 * Service interface for password management operations.
 */
public interface PasswordService {

    /**
     * Set password for first time using token from email (for new executives).
     * Validates token expiry, sets password, and activates the user account.
     * 
     * @param request the set password request containing token and password
     * @throws NotFoundException if token is invalid or already used
     * @throws SecurityException if token is expired or passwords don't match
     */
    void setPassword(SetPasswordRequest request);

    /**
     * Update password for logged-in user (UM-9).
     * Validates current password and ensures new password is different.
     * 
     * @param userEmail the email of the logged-in user
     * @param request the update password request
     * @throws NotFoundException if user not found
     * @throws SecurityException if current password is invalid or new password same as current
     */
    void updatePassword(String userEmail, UpdatePasswordRequest request);
}
