package id.go.kemenkoinfra.ipfo.sifpi.common.services;

/**
 * Service interface for sending emails.
 */
public interface EmailService {

    /**
     * Send invitation email to new executive with password setup link.
     * 
     * @param toEmail the recipient email address
     * @param name the recipient name
     * @param setupToken the password setup token
     */
    void sendExecutiveInvitation(String toEmail, String name, String setupToken);
}
