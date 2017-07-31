// Copyright (C) 2017 Hideaki Narita

package com.hideakin.app.model;

import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.IOException;
import java.io.Writer;
import java.util.List;

public class TranslationDocumentWriter implements Closeable {

    private static final String MSGID = "msgid";
    private static final String MSGSTR = "msgstr";
    private static final String NEWLINE = System.lineSeparator();

    private BufferedWriter out;

    public TranslationDocumentWriter(Writer writer) {
        out = new BufferedWriter(writer);
    }

    public void write(List<TranslationUnit> list) throws IOException {
        for (TranslationUnit tu : list) {
            writeHeader(tu.getHeader());
            write(MSGID, tu.getKey());
            write(MSGSTR, tu.getVal());
            out.write(NEWLINE);
        }
    }

    private void writeHeader(TranslationUnit.StringList ss) throws IOException {
        for (int i = 0; i < ss.size(); i++) {
            out.write(ss.get(i));
            out.write(NEWLINE);
        }
    }

    private void write(String tag, TranslationUnit.StringList ss) throws IOException {
        out.write(tag);
        if (ss.size() > 0) {
            out.write(" \"");
            out.write(ss.get(0));
            out.write("\"");
            out.write(NEWLINE);
            for (int i = 1; i < ss.size(); i++) {
                out.write("\"");
                out.write(ss.get(i));
                out.write("\"");
                out.write(NEWLINE);
            }
        } else {
            out.write(" \"\"");
            out.write(NEWLINE);
        }
    }

    @Override
    public void close() throws IOException {
        out.close();
    }

}
