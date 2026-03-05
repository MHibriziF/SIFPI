package id.go.kemenkoinfra.ipfo.sifpi.auth.listener;

import id.go.kemenkoinfra.ipfo.sifpi.auth.event.InvestorRegisteredEvent;
import id.go.kemenkoinfra.ipfo.sifpi.auth.event.OwnerRegisteredEvent;
import id.go.kemenkoinfra.ipfo.sifpi.common.config.EmailProperties;
import id.go.kemenkoinfra.ipfo.sifpi.common.services.EmailService;
import id.go.kemenkoinfra.ipfo.sifpi.common.utils.EmailTemplateUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.Map;

/**
 * Listener for registration events.
 * Sends welcome emails AFTER database transaction commits successfully.
 * This prevents email sending if registration fails or rolls back.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RegistrationEmailListener {

    private final EmailService emailService;
    private final EmailTemplateUtil emailTemplateUtil;
    private final EmailProperties emailProperties;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onInvestorRegistered(InvestorRegisteredEvent event) {
        try {
            String sectors = String.join(", ", event.request().getSectorInterest());
            
            String html = emailTemplateUtil.load("investor-registration", Map.of(
                    "name", event.user().getName(),
                    "email", event.user().getEmail(),
                    "loginUrl", emailProperties.getLoginUrl(),
                    "sectors", sectors,
                    "budget", event.request().getBudgetInvestasi() != null ? event.request().getBudgetInvestasi() : "-"
            ));

            emailService.sendEmail(
                    event.user().getEmail(),
                    "Selamat Datang - Registrasi Investor SIFPI",
                    html
            );
            log.info("Registration email sent successfully to: {}", event.user().getEmail());
        } catch (Exception e) {
            log.warn("Failed to send registration email to {}: {}", event.user().getEmail(), e.getMessage());
            // Don't throw - email delivery is best-effort, user is already registered
        }
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onOwnerRegistered(OwnerRegisteredEvent event) {
        try {
            String html = emailTemplateUtil.load("owner-registration", Map.of(
                    "name", event.user().getName(),
                    "email", event.user().getEmail(),
                    "loginUrl", emailProperties.getLoginUrl()
            ));

            emailService.sendEmail(
                    event.user().getEmail(),
                    "Selamat Datang - Registrasi Project Owner SIFPI",
                    html
            );
            log.info("Registration email sent successfully to: {}", event.user().getEmail());
        } catch (Exception e) {
            log.warn("Failed to send registration email to {}: {}", event.user().getEmail(), e.getMessage());
            // Don't throw - email delivery is best-effort, user is already registered
        }
    }
}
