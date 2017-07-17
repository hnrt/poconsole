/**
 * Copyright (C) 2017 Hideaki Narita
 */
package com.hideakin.app.model;

import java.util.ArrayList;
import java.util.List;

public class TranslationUnit {

    public static final class StringList {

        private List<String> value;

        public StringList() {
            value = new ArrayList<>();
        }

        public int size() {
            return value.size();
        }

        public String get(int index) {
            return value.get(index);
        }

        public void add(String s) {
            value.add(s);
        }

        public boolean isEmpty() {
            return size() == 0 || (size() == 1 && get(0).isEmpty());
        }

        @Override
        public boolean equals(Object other) {
            if (other == null || other.getClass() != StringList.class) {
                return false;
            }
            StringList id2 = (StringList)other;
            if (size() != id2.size()) {
                return false;
            }
            for (int index = 0; index < size(); index++) {
                if (!get(index).equals(id2.get(index))) {
                    return false;
                }
            }
            return true;
        }

        @Override
        public String toString() {
            StringBuffer buf = new StringBuffer();
            if (size() == 1) {
                buf.append(get(0));
            } else if (size() > 1) {
                if (get(0).isEmpty()) {
                    buf.append(get(1));
                    for (int i = 2; i < size(); i++) {
                        buf.append("\n");
                        buf.append(get(i));
                    }
                } else {
                    buf.append(get(0));
                    for (int i = 1; i < size(); i++) {
                        buf.append("\n");
                        buf.append(get(i));
                    }
                }
            }
            return buf.toString();
        }

        public StringList clone() {
            StringList ss = new StringList();
            for (String s : value) {
                ss.value.add(s);
            }
            return ss;
        }

        public void set(String s) {
            value.clear();
            String[] ss = s.split("\n");
            if (ss.length == 0) {
                value.add("");
            } else if (ss.length == 1) {
                value.add(ss[0]);
            } else if (ss.length > 1) {
                value.add("");
                for (String t : ss) {
                    value.add(t);
                }
            }
        }

    }

    private int line;
    private StringList header;
    private StringList msgid;
    private StringList msgstr;
    private StringList msgstrBackup;

    public TranslationUnit(int line) {
        this.line = line;
        header = new StringList();
        msgid = new StringList();
        msgstr = new StringList();
    }

    public int getLine() {
        return line;
    }

    public StringList getHeader() {
        return header;
    }

    public StringList getId() {
        return msgid;
    }

    public StringList getStr() {
        return msgstr;
    }
    
    public void createBackup() {
        msgstrBackup = msgstr.clone();
    }

    public void revert() {
        msgstr = msgstrBackup.clone();
    }

    public boolean isChanged() {
        return !msgstr.equals(msgstrBackup);
    }

    @Override
    public String toString() {
        StringBuffer buf = new StringBuffer();
        buf.append("{line:");
        buf.append(line);
        buf.append(",header:");
        for (int i = 0; i < header.size(); i++) {
            buf.append("[");
            buf.append(i);
            buf.append("]");
            buf.append(header.get(i));
        }
        buf.append(",msgid:");
        for (int i = 0; i < msgid.size(); i++) {
            buf.append("[");
            buf.append(i);
            buf.append("]\"");
            buf.append(msgid.get(i));
            buf.append("\"");
        }
        buf.append(",msgstr:");
        for (int i = 0; i < msgstr.size(); i++) {
            buf.append("[");
            buf.append(i);
            buf.append("]\"");
            buf.append(msgstr.get(i));
            buf.append("\"");
        }
        buf.append("}");
        return buf.toString();
    }

}
