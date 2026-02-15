package id.go.kemenkoinfra.ipfo.sifpi.common.security;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

@Data
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {

    private String secret;
    private long expirationMs;
    private String cookieName;
    private String cookiePath;
    private boolean cookieSecure;
    private String cookieSameSite;
}
