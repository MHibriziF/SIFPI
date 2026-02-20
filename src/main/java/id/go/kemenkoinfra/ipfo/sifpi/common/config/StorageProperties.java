package id.go.kemenkoinfra.ipfo.sifpi.common.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

@Data
@ConfigurationProperties(prefix = "storage")
public class StorageProperties {

    private String endpoint;
    private String accessKey;
    private String secretKey;
    private String bucketName;
    private String region;
}
