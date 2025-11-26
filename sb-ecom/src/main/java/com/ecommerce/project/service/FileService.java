package com.ecommerce.project.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URL;
import java.time.Duration;

public interface FileService {
    /**
     * Stores the given file and returns a public URL (or accessible path) to it.
     * The implementation must validate content type and size.
     */
    String uploadImage(MultipartFile file) throws IOException;

    /**
     * Delete an image by filename or URL; tolerate missing file (no exception).
     */
    void deleteImage(String fileOrUrl);

    /**
     * Optional helper: build a public URL from stored filename.
     */
    String buildFileUrl(String filename);
}
