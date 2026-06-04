package org.viators.argo.docs.files;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.viators.argo.common.exceptions.InvalidStateException;
import org.viators.argo.docs.config.DocStorageProperties;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
@RequiredArgsConstructor
@Slf4j
public class DocumentFileStorageService {

    private final DocStorageProperties docStorageProperties;

    private Path storageRoot;

    @PostConstruct
    void initialize() throws IOException {
        this.storageRoot = Paths.get(docStorageProperties.storagePathRoot()).toAbsolutePath().normalize();
        Files.createDirectories(storageRoot);
        log.info("Document storage initialised at {}", storageRoot);
    }

    /**
     * Writes the uploaded file to disk under the given storage key.
     *
     * @throws InvalidStateException if the write fails for any reason
     */
    public void store(MultipartFile file, String storageKey) {
        Path target = resolve(storageKey);
        try (InputStream in = file.getInputStream()) {
            Files.copy(in, target, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new InvalidStateException("Failed to store file with key: " + storageKey);
        }
    }

    public void delete(String storageKey) {
        try {
            Files.deleteIfExists(resolve(storageKey));
        } catch (IOException e) {
            log.error("Failed to delete file with key {}: {}", storageKey, e.getMessage());
        }
    }

    private Path resolve(String storageKey) {
        Path resolved = storageRoot.resolve(storageKey).normalize();
        if (!resolved.startsWith(storageRoot)) {
            throw new InvalidStateException("Storage key escapes root:" + storageKey);
        }
        return resolved;
    }

}
