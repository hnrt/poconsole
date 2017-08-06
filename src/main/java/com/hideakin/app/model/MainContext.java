// Copyright (C) 2017 Hideaki Narita

package com.hideakin.app.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Properties;

import com.hideakin.app.exception.FileIoError;
import com.hideakin.app.file.FileUtil;

public class MainContext {

    public static final String APP_NAME = "poconsole";
    public static final String APP_DISPLAY_NAME = "gettext Portable Object Console";
    public static final String APP_VERSION = "Version 1.0";
    public static final String COPYRIGHT = "Copyright (C) 2017 Hideaki Narita";

    private static final String HOME = "HOME";
    private static final String APP_DIR_NAME = "/.poconsole/";
    private static final String PROPERTIES_NAME = "poconsole.properties";
    private static final String PROPERTIES_COMMENT = "poconsole";
    private static final String OPLOG_NAME = "operation.log";
    private static final String FILE_ENCODING = "file.encoding";
    private static final Charset CHARSET = StandardCharsets.UTF_8;

    private static MainContext singleton;

    private Properties properties;
    private TranslationDocument document;
    private RecentlyUsedList recentlyUsed;

    static {
        singleton = new MainContext();
    }

    private MainContext() {
        properties = new Properties();
        recentlyUsed = new RecentlyUsedList(getOperationLogPath());
    }

    public static MainContext getInstance() {
        return singleton;
    }

    public String getProperty(String key) {
        return properties.getProperty(key);
    }

    public String getProperty(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }

    public void setProperty(String key, String value) {
        properties.setProperty(key, value);
    }

    public TranslationDocument getDocument() {
        return document;
    }
    
    public String getDocumentPath() {
        return document != null ? document.getPath() : null;
    }

    public List<String> getRecentlyUsed() {
        return recentlyUsed.getList();
    }

    public String getFileEncoding() {
        return getProperty(FILE_ENCODING);
    }

    public void init() {
        File dir = new File(getAppDirPath());
        if (!dir.isDirectory()) {
            dir.mkdirs();
        }
        try (Reader reader = new InputStreamReader(new FileInputStream(getPropertiesPath()), CHARSET)) {
            properties.load(reader);
            String encoding = getProperty(FILE_ENCODING);
            if (encoding == null) {
                setProperty(FILE_ENCODING, CHARSET.name());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        recentlyUsed.load();
    }

    public boolean quit() {
        try (Writer writer = new OutputStreamWriter(new FileOutputStream(getPropertiesPath()), CHARSET)) {
            properties.store(writer, PROPERTIES_COMMENT);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    public TranslationDocument open(String path) throws FileIoError {
        document = TranslationDocument.load(path, getFileEncoding());
        recentlyUsed.add(document.getPath());
        return document;
    }

    public TranslationDocument save() throws FileIoError {
        return save(document.getPath());
    }

    public TranslationDocument save(String path) throws FileIoError {
        String path2 = FileUtil.getTempPath(path);
        String path3 = FileUtil.getBackupPath(path);
        document.save(path2);
        try {
            File file1 = new File(path);
            File file2 = new File(path2);
            File file3 = new File(path3);
            if (file1.exists()) {
                file1.renameTo(file3);
            }
            file2.renameTo(file1);
            try (OperationLogWriter out = OperationLog.getWriter(getOperationLogPath())) {
                out.write(OperationKind.SAVE, path);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            throw new FileIoError(path, e);
        }
        return document;
    }

    private String getAppDirPath() {
        return System.getenv(HOME) + APP_DIR_NAME;
    }

    private String getPropertiesPath() {
        return getAppDirPath() + PROPERTIES_NAME;
    }

    public String getOperationLogPath() {
        return getAppDirPath() + OPLOG_NAME;
    }

}
