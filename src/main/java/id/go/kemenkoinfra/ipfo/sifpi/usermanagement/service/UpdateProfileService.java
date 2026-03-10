package id.go.kemenkoinfra.ipfo.sifpi.usermanagement.service;

import id.go.kemenkoinfra.ipfo.sifpi.usermanagement.dto.request.UpdateProfileRequest;
import id.go.kemenkoinfra.ipfo.sifpi.usermanagement.dto.UserDetailDTO;

/**
 * Service for UM-8: Update own profile.
 * Applies role-sensitive field updates for the currently authenticated user.
 */
public interface UpdateProfileService {

    /**
     * Updates the profile of the user identified by {@code currentEmail}.
     * <ul>
     *   <li>All roles: name, email (uniqueness-checked), phone number.</li>
     *   <li>PROJECT_OWNER: also updates institution name and position.</li>
     *   <li>INVESTOR: also updates company name, investment scale, and sector interests.</li>
     * </ul>
     *
     * @param currentEmail the authenticated user's current email (from JWT subject)
     * @param request      validated update payload
     * @return the refreshed {@link UserDetailDTO} reflecting the saved changes
     * @throws id.go.kemenkoinfra.ipfo.sifpi.common.exception.NotFoundException if the user no longer exists
     * @throws id.go.kemenkoinfra.ipfo.sifpi.common.exception.ConflictException  if the new email is already taken by another account
     */
    UserDetailDTO updateProfile(String currentEmail, UpdateProfileRequest request);
}
