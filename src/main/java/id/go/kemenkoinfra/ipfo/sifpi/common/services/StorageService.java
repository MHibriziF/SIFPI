package id.go.kemenkoinfra.ipfo.sifpi.common.services;

import java.io.IOException;
import java.util.UUID;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import id.go.kemenkoinfra.ipfo.sifpi.common.config.StorageProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectResponse;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Slf4j
@Service
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "storage", name = "endpoint", matchIfMissing = false)
public class StorageService {

    private final S3Client s3Client;
    private final StorageProperties storageProperties;

    public String upload(String folder, MultipartFile file) {
        String extension = getExtension(file.getOriginalFilename());
        String key = folder + "/" + UUID.randomUUID() + extension;

        try {
            PutObjectRequest request = PutObjectRequest.builder()
                    .bucket(storageProperties.getBucketName())
                    .key(key)
                    .contentType(file.getContentType())
                    .build();

            s3Client.putObject(request, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));
            log.info("File uploaded: {}", key);
            return key;
        } catch (IOException e) {
            throw new RuntimeException("Gagal mengunggah file: " + e.getMessage(), e);
        }
    }

    public byte[] download(String key) {
        try {
            GetObjectRequest request = GetObjectRequest.builder()
                    .bucket(storageProperties.getBucketName())
                    .key(key)
                    .build();

            return s3Client.getObject(request).readAllBytes();
        } catch (NoSuchKeyException e) {
            throw new id.go.kemenkoinfra.ipfo.sifpi.common.exception.NotFoundException("File", key);
        } catch (IOException e) {
            throw new RuntimeException("Gagal mengunduh file: " + e.getMessage(), e);
        }
    }

    public String getContentType(String key) {
        try {
            HeadObjectRequest request = HeadObjectRequest.builder()
                    .bucket(storageProperties.getBucketName())
                    .key(key)
                    .build();

            HeadObjectResponse response = s3Client.headObject(request);
            return response.contentType();
        } catch (NoSuchKeyException e) {
            throw new id.go.kemenkoinfra.ipfo.sifpi.common.exception.NotFoundException("File", key);
        }
    }

    public void delete(String key) {
        DeleteObjectRequest request = DeleteObjectRequest.builder()
                .bucket(storageProperties.getBucketName())
                .key(key)
                .build();

        s3Client.deleteObject(request);
        log.info("File deleted: {}", key);
    }

    public String getPublicUrl(String key) {
        String endpoint = storageProperties.getEndpoint();
        String bucket = storageProperties.getBucketName();
        return endpoint + "/" + bucket + "/" + key;
    }

    private String getExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return "";
        }
        return filename.substring(filename.lastIndexOf('.'));
    }
}
