// Copyright (C) 2017 Hideaki Narita

package com.hideakin.app.model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import com.hideakin.app.encoding.EncodingUtil;

public class TextFile {

    private String path;
    private List<String> lines;

    public TextFile(String path) throws IOException {
        this.path = path;
        this.lines = new ArrayList<>();
        File file = new File(path);
        String encoding = EncodingUtil.examine(path);
        try (BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(file), encoding))) {
            String text;
            while ((text = in.readLine()) != null) {
                lines.add(text);
            }
        } catch (IOException e) {
            throw e;
        }
    }

    public String getPath() {
        return this.path;
    }

    public String getLine(int line) {
        return this.lines.get(line);
    }

    public List<String> getLines() {
        return this.lines;
    }

    public int getLineCount() {
        return this.lines.size();
    }

}
