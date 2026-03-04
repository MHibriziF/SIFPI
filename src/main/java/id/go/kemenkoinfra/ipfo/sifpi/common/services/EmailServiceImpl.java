package id.go.kemenkoinfra.ipfo.sifpi.common.services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import id.go.kemenkoinfra.ipfo.sifpi.common.config.EmailProperties;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class EmailServiceImpl implements EmailService {

    private static final String SENDGRID_API_URL = "https://api.sendgrid.com/v3/mail/send";
    private static final int SENDGRID_MAX_BATCH = 1000;

    private final EmailProperties emailProperties;
    private final RestClient restClient;

    public EmailServiceImpl(EmailProperties emailProperties, RestClient.Builder restClientBuilder) {
        this.emailProperties = emailProperties;
        this.restClient = restClientBuilder
                .baseUrl(SENDGRID_API_URL)
                .build();
    }

    @Override
    public void sendEmail(String to, String subject, String htmlContent) {
        String payload = buildPayload(List.of(to), subject, htmlContent);
        dispatch(payload, 1);
        log.info("Email sent successfully to {}", to);
    }

    @Override
    public void sendBulkEmail(List<String> toEmails, String subject, String htmlContent) {
        if (toEmails == null || toEmails.isEmpty()) {
            log.warn("sendBulkEmail called with empty recipient list, skipping");
            return;
        }

        int totalBatches = (int) Math.ceil((double) toEmails.size() / SENDGRID_MAX_BATCH);

        for (int i = 0; i < toEmails.size(); i += SENDGRID_MAX_BATCH) {
            List<String> batch = toEmails.subList(i, Math.min(i + SENDGRID_MAX_BATCH, toEmails.size()));
            String payload = buildPayload(batch, subject, htmlContent);
            dispatch(payload, batch.size());
            log.info("Bulk email batch {}/{} sent ({} recipients)",
                    (i / SENDGRID_MAX_BATCH) + 1, totalBatches, batch.size());
        }
    }

    private void dispatch(String payload, int recipientCount) {
        try {
            restClient.post()
                    .header("Authorization", "Bearer " + emailProperties.getSendgridApiKey())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(payload)
                    .retrieve()
                    .toBodilessEntity();
        } catch (Exception e) {
            log.error("Failed to send email to {} recipient(s): {}", recipientCount, e.getMessage());
            throw new RuntimeException("Gagal mengirim email. Silakan coba lagi atau hubungi administrator.", e);
        }
    }

    private String buildPayload(List<String> emails, String subject, String htmlContent) {
        String escapedContent = htmlContent
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");

        String personalizations = emails.stream()
                .map(email -> "{\"to\": [{\"email\": \"" + email.replace("\"", "\\\"") + "\"}]}")
                .collect(Collectors.joining(",\n            "));

        return """
            {
                "personalizations": [
                    %s
                ],
                "from": {
                    "email": "%s",
                    "name": "%s"
                },
                "subject": "%s",
                "content": [
                    {
                        "type": "text/html",
                        "value": "%s"
                    }
                ]
            }
            """.formatted(
                personalizations,
                emailProperties.getFromAddress(),
                emailProperties.getFromName(),
                subject,
                escapedContent
            );
    }
}
