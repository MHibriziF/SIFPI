package id.go.kemenkoinfra.ipfo.sifpi.common.utils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

/**
 * Loads HTML email templates from {@code classpath:templates/email/} and
 * substitutes {@code {{key}}} placeholders with the provided variable map.
 *
 * <p>Template files must live at:
 * {@code src/main/resources/templates/email/<name>.html}
 */
@Component
public class EmailTemplateUtil {

    public String load(String templateName, Map<String, String> variables) {
        ClassPathResource resource = new ClassPathResource("templates/email/" + templateName + ".html");

        try (InputStream is = resource.getInputStream()) {
            String content = new String(is.readAllBytes(), StandardCharsets.UTF_8);
            for (Map.Entry<String, String> entry : variables.entrySet()) {
                content = content.replace("{{" + entry.getKey() + "}}", entry.getValue());
            }
            return content;
        } catch (IOException e) {
            throw new RuntimeException("Gagal memuat template email: " + templateName, e);
        }
    }
}
