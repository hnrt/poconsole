/**
 * Copyright (C) 2017 Hideaki Narita
 */
package com.hideakin.app.exception;

public class FileIoError extends Exception {

    private static final long serialVersionUID = 1L;

    private String path;

    public FileIoError(String path, String message) {
        super(message);
        this.path = path;
    }

    public FileIoError(String path, String message, Throwable cause) {
        super(message, cause);
        this.path = path;
    }

    public FileIoError(String path, Throwable cause) {
        super(cause);
        this.path = path;
    }

    public String getPath() {
        return path;
    }

}
