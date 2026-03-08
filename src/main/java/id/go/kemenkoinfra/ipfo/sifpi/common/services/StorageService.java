package id.go.kemenkoinfra.ipfo.sifpi.common.services;

import org.springframework.web.multipart.MultipartFile;

public interface StorageService {

    String upload(String folder, MultipartFile file);

    String uploadFromUrl(String folder, String sourceUrl);

    FileDownload download(String key);

    void delete(String key);

    String getPublicUrl(String key);

    record FileDownload(byte[] data, String contentType) {}
}
