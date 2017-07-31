// Copyright (C) 2017 Hideaki Narita

package com.hideakin.app.model;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import com.hideakin.app.encoding.EncodingUtil;
import com.hideakin.app.exception.FileIoError;
import com.hideakin.app.file.FileUtil;

public class MainContext {

    private static final String HOME = "HOME";
    private static final String APP_DIR_NAME = "/.poconsole/";
    private static final String PROPERTIES_NAME = "poconsole.properties";
    private static final String PROPERTIES_COMMENT = "poconsole";
    private static final String DEFAULT_CS = EncodingUtil.UTF_8;
    private static final String RUFL_NAME = "RecentlyUsed.lst";
    private static final String FILE_ENCODING = "file.encoding";

    private static MainContext singleton;

    public static final class RecentlyUsedList {

        private static final String SEPARATOR = "\t";
        private static final String NEWLINE = System.lineSeparator();
        public static final int MAX_COUNT = 10;

        private String path;
        private List<String> list;
        private boolean loading;

        public RecentlyUsedList(String path) {
            this.path = path;
            this.list = new ArrayList<>();
            this.loading = false;
        }

        public List<String> getList() {
            return list;
        }

        public void load() {
            loading = true;
            try (BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(path), DEFAULT_CS))) {
                String text;
                while ((text = in.readLine()) != null) {
                    String[] portions = text.split(SEPARATOR);
                    if (portions.length == 2) {
                        File file = new File(portions[1]);
                        if (file.exists()) {
                            add(file.getCanonicalPath());
                        }
                    }
                }
            } catch (IOException e) {
            }
            loading = false;
        }

        public void add(String path) {
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).equals(path)) {
                    if (i > 0) {
                        list.remove(i);
                        list.add(0, path);
                        if (!loading) {
                            save(path);
                        }
                    }
                    return;
                }
            }
            list.add(0, path);
            if (list.size() > MAX_COUNT) {
                list.remove(MAX_COUNT);
            }
            if (!loading) {
                save(path);
            }
        }

        private void save(String entry) {
            try (BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(path, true), StandardCharsets.UTF_8))) {
                LocalDateTime dt = LocalDateTime.now();
                out.write(String.format("%d-%02d-%02d %02d:%02d:%02d",
                        dt.getYear(), dt.getMonthValue(), dt.getDayOfMonth(),
                        dt.getHour(), dt.getMinute(), dt.getSecond()));
                out.write(SEPARATOR);
                out.write(entry);
                out.write(NEWLINE);
            } catch (IOException e) {
            }
        }

    }

    private Properties properties;
    private TranslationDocument document;
    private RecentlyUsedList recentlyUsed;

    static {
        singleton = new MainContext();
    }

    private MainContext() {
        properties = new Properties();
        recentlyUsed = new RecentlyUsedList(getRecentlyUsedFileListPath());
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
        try (Reader reader = new InputStreamReader(new FileInputStream(getPropertiesPath()), DEFAULT_CS)) {
            properties.load(reader);
            String encoding = getProperty(FILE_ENCODING);
            if (encoding == null) {
                setProperty(FILE_ENCODING, DEFAULT_CS);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        recentlyUsed.load();
    }

    public boolean quit() {
        try (Writer writer = new OutputStreamWriter(new FileOutputStream(getPropertiesPath()), StandardCharsets.UTF_8)) {
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

    private String getRecentlyUsedFileListPath() {
        return getAppDirPath() + RUFL_NAME;
    }

}
