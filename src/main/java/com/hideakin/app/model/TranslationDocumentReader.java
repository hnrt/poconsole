/**
 * Copyright (C) 2017 Hideaki Narita
 */
package com.hideakin.app.model;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.Reader;

import com.hideakin.app.exception.ParseError;

public class TranslationDocumentReader implements Closeable {

    private static final String MSGID = "msgid";
    private static final String MSGSTR = "msgstr";
    private static final String HEADER_LEADER = "#";
    private static final char SPACE = ' ';
    private static final char QUOTATION_MARK = '\"';
    private static final char BACKSLASH = '\\';
    private static final int STATE_START_PENDING = 0;
    private static final int STATE_HEADER = 1;
    private static final int STATE_MSGID = 2;
    private static final int STATE_MSGSTR = 3;
    private static final String ERROR_MSGID = "msgid is missing.";
    private static final String ERROR_MSGID_VALUE = "msgid: Quoted string is invalid or missing.";
    private static final String ERROR_MSGSTR = "msgstr is missing.";
    private static final String ERROR_MSGSTR_VALUE = "msgstr: Quoted string is invalid or missing.";
    private static final String ERROR_EOS = "Unexpected end of stream.";

    private BufferedReader in;
    private String text;
    private int line;

    public TranslationDocumentReader(Reader reader) throws IOException {
        in = new BufferedReader(reader);
        text = in.readLine();
        line = 1;
    }

    public TranslationUnit read() throws ParseError, IOException {
        TranslationUnit tu = null;
        int state = STATE_START_PENDING;
        while (text != null) {
            switch (state) {
            case STATE_START_PENDING:
                if (text.isEmpty()) {
                    break;
                }
                tu = new TranslationUnit(line);
                state = STATE_HEADER;
                //FALLTHROUGH
            case STATE_HEADER:
                if (text.startsWith(HEADER_LEADER)) {
                    tu.getHeader().add(text);
                } else if (text.startsWith(MSGID)) {
                    state = STATE_MSGID;
                    String s = parseQuotedString(text, parseStartOfQuotedString(text, MSGID.length()));
                    if (s != null) {
                        tu.getId().add(s);
                    } else {
                        throw new ParseError(line, ERROR_MSGID_VALUE);
                    }
                } else {
                    throw new ParseError(line, ERROR_MSGID);
                }
                break;
            case STATE_MSGID:
                if (!text.isEmpty() && text.charAt(0) == QUOTATION_MARK) {
                    String s = parseQuotedString(text, 1);
                    if (s != null) {
                        tu.getId().add(s);
                    } else {
                        throw new ParseError(line, ERROR_MSGID_VALUE);
                    }
                } else if (text.startsWith(MSGSTR)) {
                    state = STATE_MSGSTR;
                    String s = parseQuotedString(text, parseStartOfQuotedString(text, MSGSTR.length()));
                    if (s != null) {
                        tu.getStr().add(s);
                    } else {
                        throw new ParseError(line, ERROR_MSGSTR_VALUE);
                    }
                } else {
                    throw new ParseError(line, ERROR_MSGSTR);
                }
                break;
            case STATE_MSGSTR:
                if (!text.isEmpty() && text.charAt(0) == QUOTATION_MARK) {
                    String s = parseQuotedString(text, 1);
                    if (s != null) {
                        tu.getStr().add(s);
                    } else {
                        throw new ParseError(line, ERROR_MSGSTR_VALUE);
                    }
                } else {
                    tu.makeBackup();
                    return tu;
                }
                break;
            }
            text = in.readLine();
            line++;
        }
        switch (state)
        {
        case STATE_START_PENDING:
            return null;
        case STATE_MSGSTR:
            tu.makeBackup();
            return tu;
        default:
            throw new ParseError(line, ERROR_EOS);
        }
    }

    @Override
    public void close() throws IOException {
        in.close();
    }

    private static int parseStartOfQuotedString(String str, int index) {
        int end = str.length();
        if (index < end && str.charAt(index) == SPACE) {
            index++;
        } else {
            return -1;
        }
        while (index < end && str.charAt(index) == SPACE) {
            index++;
        }
        if (index < end && str.charAt(index) == QUOTATION_MARK) {
            index++;
        } else {
            return -1;
        }
        return index < end ? index : -1;
    }

    private static String parseQuotedString(String str, int index) {
        if (index < 0) {
            return null;
        }
        int start = index;
        int end = str.length();
        while (true) {
            if (index >= end) {
                return null;
            }
            char c = str.charAt(index);
            if (c == QUOTATION_MARK) {
                break;
            } else if (c == BACKSLASH) {
                index += 2;
            } else {
                index++;
            }
        }
        return str.substring(start, index);
    }

}
