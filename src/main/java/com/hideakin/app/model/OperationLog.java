// Copyright (C) 2017 Hideaki Narita

package com.hideakin.app.model;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class OperationLog {

    public static final Charset CHARSET = StandardCharsets.UTF_8;
    public static final String SEPARATOR = "\t";
    public static final String NEWLINE = System.lineSeparator();

    public static OperationLogReader getReader(String path) throws IOException {
        return new OperationLogReader(MainContext.getInstance().getOperationLogPath());
    }

    public static OperationLogWriter getWriter(String path) throws IOException {
        return new OperationLogWriter(MainContext.getInstance().getOperationLogPath());
    }

}
