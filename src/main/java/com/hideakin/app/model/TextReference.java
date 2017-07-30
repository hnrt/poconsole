/**
 * Copyright (C) 2017 Hideaki Narita
 */
package com.hideakin.app.model;

import java.io.File;

public class TextReference {

    private String path;
    private int line;

    public TextReference(String path, int line) {
        this.path = path;
        this.line = line;
    }

    public String getPath() {
        return path;
    }

    public int getLine() {
        return line;
    }

    public String getName() {
        return new File(path).getName();
    }

    @Override
    public String toString() {
        return path + ":" + line;
    }

    public static TextReference parse(String value) {
        String[] ss = value.split(":");
        return new TextReference(ss[0], Integer.parseInt(ss[1]));
    }

}
