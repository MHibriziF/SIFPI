package id.go.kemenkoinfra.ipfo.sifpi.auth.service;

/**
 * Service for email verification flow
 */
public interface EmailVerificationService {

    /**
     * Verify email using verification token
     * @param token the email verification token
     * @throws IllegalArgumentException if token is invalid or expired
     */
    void verifyEmail(String token);

    /**
     * Resend verification email for a user
     * @param email the user's email
     * @throws NotFoundException if user not found with email
     * @throws IllegalArgumentException if email already verified
     */
    void resendVerificationEmail(String email);
}
