# EmailService Usage Guide

`EmailService` is a common transport service for sending HTML emails via SendGrid. It has one job: **send a pre-built HTML email** to a recipient.

Building the email content — loading the template, filling in variables, constructing the subject — is the responsibility of **each feature service** that needs to send an email.

## Available Methods

| Method | Params | Returns | Description |
|--------|--------|---------|-------------|
| `sendEmail(to, subject, htmlContent)` | `String to`, `String subject`, `String htmlContent` | `void` | Sends an HTML email to a single recipient |
| `sendBulkEmail(toEmails, subject, htmlContent)` | `List<String> toEmails`, `String subject`, `String htmlContent` | `void` | Blasts the same HTML email to many recipients |

Both methods throw `RuntimeException` (Indonesian message) on SendGrid failure, which causes the enclosing `@Transactional` method to roll back.

## HTML Templates

Store email templates as `.html` files in:

```
src/main/resources/templates/email/<template-name>.html
```

Use `{{variableName}}` placeholders for dynamic values. Load and render them in your service using `EmailTemplateUtil`.

## EmailTemplateUtil

`EmailTemplateUtil` is a Spring component in `common/utils/` that loads a template from the classpath and replaces `{{key}}` placeholders.

```java
emailTemplateUtil.load("template-name", Map.of(
    "key1", "value1",
    "key2", "value2"
));
```

- Template name maps to `templates/email/<name>.html`
- Throws `RuntimeException` if the template file doesn't exist

## Injecting into Your Service

```java
@Service
@RequiredArgsConstructor
public class SomeFeatureServiceImpl implements SomeFeatureService {

    private final EmailService emailService;
    private final EmailTemplateUtil emailTemplateUtil;
    private final EmailProperties emailProperties; // only if you need loginUrl or other email config
}
```

## Sending an Email — Full Example

### 1. Create the template

`src/main/resources/templates/email/project-approved.html`:

```html
<!DOCTYPE html>
<html lang="id">
<head><meta charset="UTF-8"></head>
<body>
    <p>Yth. <strong>{{ownerName}}</strong>,</p>
    <p>Proyek <strong>{{projectName}}</strong> Anda telah disetujui.</p>
    <p><a href="{{projectUrl}}">Lihat Detail Proyek</a></p>
</body>
</html>
```

### 2. Build and send in your service

```java
@Transactional
public ProjectDTO approveProject(UUID projectId) {
    Project project = projectRepository.findById(projectId)
            .orElseThrow(() -> new NotFoundException("Project", projectId));

    project.setStatus(ProjectStatus.APPROVED);
    projectRepository.save(project);

    sendApprovalEmail(project);

    return projectMapper.toDTO(project);
}

private void sendApprovalEmail(Project project) {
    String html = emailTemplateUtil.load("project-approved", Map.of(
            "ownerName", project.getOwner().getName(),
            "projectName", project.getName(),
            "projectUrl", "https://sifpi.example.com/projects/" + project.getId()
    ));

    emailService.sendEmail(
            project.getOwner().getEmail(),
            "Proyek Anda Telah Disetujui - SIFPI",
            html
    );
}
```

## Blasting Email to Many Recipients

Use `sendBulkEmail` when you want to send the **same content** to a list of recipients. SendGrid processes up to 1,000 addresses per API request — larger lists are automatically split into batches internally, so you don't need to think about it.

### When to use which method

| Scenario | Method |
|----------|--------|
| Single recipient (transactional email — invitation, approval, etc.) | `sendEmail` |
| Same content to many recipients (announcements, newsletters) | `sendBulkEmail` |
| Different personalised content per recipient | Call `sendEmail` in a loop from your feature service |

### Example

```java
@Transactional
public void announceNewProject(UUID projectId) {
    Project project = projectRepository.findById(projectId)
            .orElseThrow(() -> new NotFoundException("Project", projectId));

    // Collect all investor emails
    List<String> investorEmails = investorRepository.findAllActive()
            .stream()
            .map(Investor::getEmail)
            .toList();

    String html = emailTemplateUtil.load("new-project-announcement", Map.of(
            "projectName", project.getName(),
            "projectUrl", "https://sifpi.example.com/projects/" + project.getId()
    ));

    emailService.sendBulkEmail(investorEmails, "Proyek Baru Tersedia - SIFPI", html);
}
```

> **Note:** `sendBulkEmail` sends the **exact same HTML** to everyone. If you need per-recipient values in the template (e.g. each investor's name), either render and send individually in a loop, or use a template with no personalized variables.

## Key Conventions

- **`EmailService` only sends** — never put template logic or business context into `EmailServiceImpl`.
- **One template per email type** — name it clearly after what it communicates, e.g. `executive-invitation.html`, `project-approved.html`.
- **Keep email building in a private method** — e.g. `sendApprovalEmail(...)` — to keep the main service method readable.
- **Email failure rolls back the transaction** — `sendEmail` throws on SendGrid error, which propagates up through the `@Transactional` boundary. This is intentional: if the email can't be sent, the record should not be persisted.
- **Log tokens in dev** — if your email contains a one-time token (setup link, reset link), log it at `WARN` level so it's accessible during local development without needing a real email inbox.

## Environment Variables

| Variable | Description |
|----------|-------------|
| `SENDGRID_API_KEY` | SendGrid API key (required) |
| `EMAIL_FROM_ADDRESS` | Sender email address (default: `noreply@mhibrizif.site`) |
| `EMAIL_FROM_NAME` | Sender display name (default: `SIFPI`) |
| `APP_LOGIN_URL` | Frontend login URL — used to construct deep links (default: `http://localhost:3000/login`) |

All values are bound to `EmailProperties` under the `app.email` config prefix.
