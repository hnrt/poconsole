/**
 * Copyright (C) 2017 Hideaki Narita
 */
package com.hideakin.app.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import com.hideakin.app.exception.FileIoError;
import com.hideakin.app.exception.ParseError;

public class TranslationDocument {

    private static final boolean DEBUG = false;

    private String path;
    private String encoding;
    private List<TranslationUnit> tuList;

    private TranslationDocument(String path) {
        this.path = path;
        tuList = new ArrayList<>();
    }

    public String getPath() {
        return path;
    }

    public List<TranslationUnit> getTranslationUnit() {
        return tuList;
    }

    public static TranslationDocument load(String path, String encoding) throws FileIoError {
        File file = new File(path);
        if (!file.exists()) {
            throw new FileIoError(path, "File not exists.");
        }
        if (!file.canRead()) {
            throw new FileIoError(path, "File not readable.");
        }
        try (TranslationDocumentReader in = new TranslationDocumentReader(new InputStreamReader(new FileInputStream(file), encoding))) {
            TranslationDocument doc = new TranslationDocument(file.getCanonicalPath());
            TranslationUnit tu;
            while ((tu = in.read()) != null) {
                doc.tuList.add(tu);
            }
            if (DEBUG) {
                for (int index = 0; index < doc.tuList.size(); index++) {
                    tu = doc.tuList.get(index);
                    System.out.println(tu.toString());
                }
            }
            doc.encoding = encoding;
            return doc;
        } catch (UnsupportedEncodingException e) {
            throw new FileIoError(path, "Unsupported encoding.", e);
        } catch (FileNotFoundException e) {
            throw new FileIoError(path, e);
        } catch (IOException e) {
            throw new FileIoError(path, e);
        } catch (ParseError e) {
            throw new FileIoError(path, "Line " + e.getLine() + ": " + e.getMessage(), e);
        }
    }

    public boolean isChanged() {
        for (TranslationUnit tu : tuList) {
            if (tu.isChanged()) {
                return true;
            }
        }
        return false;
    }

    public void save(String path) throws FileIoError {
        try (TranslationDocumentWriter out = new TranslationDocumentWriter(new OutputStreamWriter(new FileOutputStream(path), Charset.forName(encoding)))) {
            out.write(tuList);
        } catch (FileNotFoundException e) {
            throw new FileIoError(path, e);
        } catch (IOException e) {
            throw new FileIoError(path, e);
        } catch (Exception e) {
            throw new FileIoError(path, e);
        }
    }

    public void createBackup() {
        for (TranslationUnit tu : tuList) {
            tu.createBackup();
        }
    }

}
