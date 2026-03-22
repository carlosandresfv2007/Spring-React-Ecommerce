package com.ecommerce.project.service;

import com.ecommerce.project.exceptions.BadRequestException;
import com.ecommerce.project.exceptions.FileStorageException;
import com.ecommerce.project.service.FileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.*;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@Service
@Slf4j
public class FileServiceImpl implements FileService {

    private static final List<String> ALLOWED_TYPES = List.of("image/png", "image/jpeg", "image/webp");
    private static final long MAX_BYTES = 5 * 1024 * 1024; // 5MB

    private final Path uploadDir;
    private final String baseUrl; // optional, e.g. http://localhost:8080/images/

    public FileServiceImpl(
            @Value("${app.upload.dir:${user.dir}/images}") String uploadDir,
            @Value("${app.image.base-url:}") String baseUrl) {

        this.uploadDir = Paths.get(uploadDir).toAbsolutePath().normalize();
        this.baseUrl = baseUrl != null ? baseUrl.trim() : "";

        try {
            Files.createDirectories(this.uploadDir);
        } catch (IOException e) {
            throw new FileStorageException("creating upload directory", this.uploadDir.toString(), e);

        }
    }

    @Override
    public String uploadImage(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new BadRequestException("image", "file is required");
        }

        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_TYPES.contains(contentType)) {
            throw new BadRequestException("contentType", "Invalid type. Allowed: " + ALLOWED_TYPES);
        }
        if (file.getSize() > MAX_BYTES) {
            throw new BadRequestException(
                    "size",
                    "Exceeded max " + (MAX_BYTES / 1024 / 1024) + " MB"
            );
        }
        // sanitize original filename
        String original = Objects.requireNonNull(file.getOriginalFilename()).trim();
        String sanitized = Paths.get(original).getFileName().toString(); // remove path parts
        int dot = sanitized.lastIndexOf('.');
        String ext = dot >= 0 ? sanitized.substring(dot) : "";

        ext = "";

        dot = sanitized.lastIndexOf('.');
        if (dot > 0 && dot < sanitized.length() - 1) {
            ext = sanitized.substring(dot).toLowerCase();
        }

        if (ext.isBlank()) {
            ext = switch (contentType) {
                case "image/png" -> ".png";
                case "image/jpeg" -> ".jpg";
                case "image/webp" -> ".webp";
                default -> throw new BadRequestException(
                        "Invalid image type. Allowed: ", ALLOWED_TYPES.toString());};
        }


        String filename = UUID.randomUUID().toString() + ext.toLowerCase();
        Path target = this.uploadDir.resolve(filename);

        // Save file atomically
        try (InputStream in = file.getInputStream()) {
            Files.copy(in, target, StandardCopyOption.REPLACE_EXISTING);
            // Optionally set file perms (POSIX systems only, ignore if not supported)
            try {
                // only attempt on systems that support posix
                Set<PosixFilePermission> perms = PosixFilePermissions.fromString("rw-r--r--");
                Files.setPosixFilePermissions(target, perms);
            } catch (UnsupportedOperationException ignored) { /* windows, skip */ }
        } catch (IOException ex) {
            throw new FileStorageException("storing file", filename, ex);
        }

        return filename; // note: not URL — use buildFileUrl(...) to expose
    }

    @Override
    public void deleteImage(String fileOrUrl) {
        if (fileOrUrl == null || fileOrUrl.isBlank()) return;

        String filename = extractFilename(fileOrUrl);
        if (filename == null) return;

        Path file = uploadDir.resolve(filename);
        try {
            Files.deleteIfExists(file);
        } catch (IOException e) {
            log.warn("Could not delete file {}: {}", file, e.getMessage());
        }
    }

    @Override
    public String buildFileUrl(String filename) {
        if (filename == null) return null;
        if (!baseUrl.isBlank()) {
            // ensure slash
            return baseUrl.endsWith("/") ? baseUrl + filename : baseUrl + "/" + filename;
        }
        // fallback to absolute local path (useful for testing)
        return uploadDir.resolve(filename).toString();
    }

    private String extractFilename(String fileOrUrl) {
        if (fileOrUrl == null) return null;
        // if it's a URL with baseUrl, strip to filename
        if (!baseUrl.isBlank() && fileOrUrl.contains(baseUrl)) {
            int idx = fileOrUrl.lastIndexOf('/');
            return fileOrUrl.substring(idx + 1);
        }
        // if it's a full path, get the filename
        return Paths.get(fileOrUrl).getFileName().toString();
    }
}
