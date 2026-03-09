package id.go.kemenkoinfra.ipfo.sifpi.usermanagement.service;

import id.go.kemenkoinfra.ipfo.sifpi.usermanagement.dto.UserDetailDTO;

/**
 * Service for UM-7: Read detail account (admin only).
 */
public interface AdminUserDetailService {

    /**
     * Returns full detail of a user, including role-specific fields and audit trail.
     *
     * @param email the user's email address
     * @return populated {@link UserDetailDTO}
     * @throws id.go.kemenkoinfra.ipfo.sifpi.common.exception.NotFoundException if no user with that email exists
     */
    UserDetailDTO getUserDetailByEmail(String email);
}
