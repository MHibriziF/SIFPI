package id.go.kemenkoinfra.ipfo.sifpi.common.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

import lombok.Data;

@Data
@Configuration
@EnableAsync
@ConfigurationProperties(prefix = "app.email")
@EnableConfigurationProperties
public class EmailProperties {

    private String sendgridApiKey;
    private String fromAddress;
    private String fromName;
    private String loginUrl;
}
