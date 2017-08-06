// Copyright (C) 2017 Hideaki Narita

package com.hideakin.app.model;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class OperationLogReader implements Closeable {

    private static final String BAD_FORMAT = "Bad operation log format.";

    private BufferedReader in;

    public OperationLogReader(String path) throws IOException {
        in = new BufferedReader(new InputStreamReader(new FileInputStream(path), OperationLog.CHARSET));       
    }

    @Override
    public void close() throws IOException {
        in.close();
    }

    public Operation read() throws IOException {
        String text = in.readLine();
        if (text == null) {
            return null;
        }
        String[] portions = text.split(OperationLog.SEPARATOR);
        if (portions.length == 3) {
            return new Operation(portions[1], portions[2]);
        } else {
            throw new RuntimeException(BAD_FORMAT);
        }
    }

}
