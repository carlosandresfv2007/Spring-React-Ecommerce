package com.ecommerce.project.exceptions;

public class FileStorageException extends RuntimeException {

    private final String action;
    private final String fileName;

    public FileStorageException(String action, String fileName) {
        super(String.format("Error while %s file: %s", action, fileName));
        this.action = action;
        this.fileName = fileName;
    }

    public FileStorageException(String action, String fileName, Throwable cause) {
        super(String.format("Error while %s file: %s | Details: %s", action, fileName, cause.getMessage()), cause);
        this.action = action;
        this.fileName = fileName;
    }

    public String getAction() {
        return action;
    }

    public String getFileName() {
        return fileName;
    }
}
