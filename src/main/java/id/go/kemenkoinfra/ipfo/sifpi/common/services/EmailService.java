package id.go.kemenkoinfra.ipfo.sifpi.common.services;

import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import id.go.kemenkoinfra.ipfo.sifpi.common.config.EmailProperties;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class EmailService {

    private static final String SENDGRID_API_URL = "https://api.sendgrid.com/v3/mail/send";

    private final EmailProperties emailProperties;
    private final RestClient restClient;

    public EmailService(EmailProperties emailProperties, RestClient.Builder restClientBuilder) {
        this.emailProperties = emailProperties;
        this.restClient = restClientBuilder
                .baseUrl(SENDGRID_API_URL)
                .build();
    }

    /**
     * Send invitation email to new executive with password setup link.
     */
    @Async
    public void sendExecutiveInvitation(String toEmail, String name, String setupToken) {
        String subject = "Undangan Akun Executive - SIFPI";
        String htmlContent = buildExecutiveInvitationEmail(name, toEmail, setupToken);

        sendEmail(toEmail, subject, htmlContent);
    }

    private void sendEmail(String to, String subject, String htmlContent) {
        String payload = buildSendGridPayload(to, subject, htmlContent);

        try {
            restClient.post()
                    .header("Authorization", "Bearer " + emailProperties.getSendgridApiKey())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(payload)
                    .retrieve()
                    .toBodilessEntity();

            log.info("Email sent successfully to {}", to);
        } catch (Exception e) {
            log.error("Failed to send email to {}: {}", to, e.getMessage(), e);
            throw new RuntimeException("Gagal mengirim email: " + e.getMessage(), e);
        }
    }

    private String buildSendGridPayload(String to, String subject, String htmlContent) {
        String escapedContent = htmlContent
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");

        return """
            {
                "personalizations": [
                    {
                        "to": [{"email": "%s"}]
                    }
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
                to,
                emailProperties.getFromAddress(),
                emailProperties.getFromName(),
                subject,
                escapedContent
            );
    }

    private String buildExecutiveInvitationEmail(String name, String email, String setupToken) {
        // Build set password URL (frontend page)
        String setPasswordUrl = emailProperties.getLoginUrl().replace("/login", "/set-password?token=" + setupToken);

        return """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <style>
                    body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
                    .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                    .header { background-color: #1a56db; color: white; padding: 20px; text-align: center; }
                    .content { padding: 20px; background-color: #f9fafb; }
                    .info-box { background-color: #fff; border: 1px solid #e5e7eb; padding: 15px; margin: 20px 0; border-radius: 8px; }
                    .info-box p { margin: 8px 0; }
                    .label { font-weight: bold; color: #374151; }
                    .value { font-family: monospace; background-color: #f3f4f6; padding: 4px 8px; border-radius: 4px; }
                    .button { display: inline-block; background-color: #1a56db; color: white; padding: 12px 24px; text-decoration: none; border-radius: 6px; margin-top: 20px; }
                    .footer { text-align: center; padding: 20px; color: #6b7280; font-size: 12px; }
                    .notice { background-color: #dbeafe; border: 1px solid #3b82f6; padding: 12px; border-radius: 6px; margin-top: 15px; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>Selamat Datang di SIFPI</h1>
                    </div>
                    <div class="content">
                        <p>Yth. <strong>%s</strong>,</p>
                        <p>Akun Executive Anda telah berhasil dibuat di Sistem Informasi Fasilitasi Proyek Infrastruktur (SIFPI).</p>
                        
                        <div class="info-box">
                            <h3 style="margin-top: 0;">Informasi Akun</h3>
                            <p><span class="label">Email:</span> <span class="value">%s</span></p>
                            <p><span class="label">Role:</span> <span class="value">Executive</span></p>
                        </div>

                        <p>Untuk mengaktifkan akun Anda, silakan klik tombol di bawah ini untuk membuat password:</p>

                        <center>
                            <a href="%s" class="button">Buat Password</a>
                        </center>

                        <div class="notice">
                            <strong>ℹ️ Informasi:</strong> Link ini akan kedaluwarsa dalam 24 jam. Jika link sudah tidak berlaku, silakan hubungi Administrator untuk mengirim ulang undangan.
                        </div>
                    </div>
                    <div class="footer">
                        <p>Email ini dikirim secara otomatis. Mohon tidak membalas email ini.</p>
                        <p>&copy; 2026 SIFPI - Kementerian Koordinator Bidang Infrastruktur</p>
                    </div>
                </div>
            </body>
            </html>
            """.formatted(name, email, setPasswordUrl);
    }
}
