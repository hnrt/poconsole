// Copyright (C) 2017 Hideaki Narita

package com.hideakin.app.file;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class FileFinder {

    private final int maxDirs;
    private int remaining;
    private String filename;

    public FileFinder(int maxDirs) {
        if (maxDirs < 1) {
            throw new RuntimeException("FileFinder(" + maxDirs + ")");
        }
        this.maxDirs = maxDirs;
    }

    public String run(String filename, String basePath) {
        try {
            this.remaining = this.maxDirs;
            this.filename = filename;
            File baseFile = new File(basePath);
            return find(baseFile.getParentFile(), baseFile);
        } catch (IOException e) {
            return null;
        }
    }

    private String find(File current, File prev) throws IOException {
        if (current == null) {
            throw new FileNotFoundException();
        }
        File[] files = current.listFiles();
        if (files != null) {
            for (File file : files) {
                String path = match(file);
                if (path != null) {
                    return path;
                }
            }
        }
        if (--this.remaining <= 0) {
            throw new FileNotFoundException();
        }
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory() && !prev.equals(file)) {
                    String path = find(file);
                    if (path != null) {
                        return path;
                    }
                }
            }
        }
        return find(current.getParentFile(), current);
    }

    private String find(File current) throws IOException {
        File[] files = current.listFiles();
        if (files != null) {
            for (File file : files) {
                String path = match(file);
                if (path != null) {
                    return path;
                }
            }
        }
        if (--this.remaining <= 0) {
            throw new FileNotFoundException();
        }
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    String path = find(file);
                    if (path != null) {
                        return path;
                    }
                }
            }
        }
        return null;
    }

    private String match(File file) throws IOException {
        String canonPath = file.getCanonicalPath();
        if (canonPath.length() == this.filename.length()) {
            if (canonPath.compareTo(this.filename) == 0) {
                return canonPath;
            }
        } else if (canonPath.length() > this.filename.length() &&
                canonPath.charAt(canonPath.length() - this.filename.length() - 1) == File.separatorChar) {
            if (canonPath.endsWith(this.filename)) {
                return canonPath;
            }
        }
        return null;
    }

}
