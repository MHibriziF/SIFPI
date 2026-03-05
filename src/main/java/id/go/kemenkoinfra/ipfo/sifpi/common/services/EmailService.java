package id.go.kemenkoinfra.ipfo.sifpi.common.services;

import java.util.List;

public interface EmailService {

    /**
     * Send an HTML email to a single recipient.
     *
     * @param to          recipient email address
     * @param subject     email subject
     * @param htmlContent fully-rendered HTML body
     */
    void sendEmail(String to, String subject, String htmlContent);

    /**
     * Blast the same HTML email to many recipients in one or more SendGrid requests.
     * SendGrid allows up to 1000 personalizations per request; larger lists are
     * automatically split into batches.
     *
     * @param toEmails    list of recipient email addresses
     * @param subject     email subject
     * @param htmlContent fully-rendered HTML body (same content for all recipients)
     */
    void sendBulkEmail(List<String> toEmails, String subject, String htmlContent);
}
