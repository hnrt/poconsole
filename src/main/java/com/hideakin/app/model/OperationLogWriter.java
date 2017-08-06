// Copyright (C) 2017 Hideaki Narita

package com.hideakin.app.model;

import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.time.LocalDateTime;

public class OperationLogWriter implements Closeable {

    private BufferedWriter out;

    public OperationLogWriter(String path) throws IOException {
        out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(path, true), OperationLog.CHARSET));
    }

    @Override
    public void close() throws IOException {
        out.close();
    }

    public void write(OperationKind kind, String path) throws IOException {
        LocalDateTime dt = LocalDateTime.now();
        out.write(String.format("%d-%02d-%02d %02d:%02d:%02d",
                dt.getYear(), dt.getMonthValue(), dt.getDayOfMonth(),
                dt.getHour(), dt.getMinute(), dt.getSecond()));
        out.write(OperationLog.SEPARATOR);
        out.write(kind.name());
        out.write(OperationLog.SEPARATOR);
        out.write(path);
        out.write(OperationLog.NEWLINE);
    }

}
