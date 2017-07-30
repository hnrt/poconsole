/**
 * Copyright (C) 2017 Hideaki Narita
 */
package com.hideakin.app.model;

import java.util.ArrayList;
import java.util.List;

import com.hideakin.app.file.FileUtil;

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
            StringBuilder buf = new StringBuilder();
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
    private StringList key;
    private StringList val;
    private StringList backup;

    public TranslationUnit(int line) {
        this.line = line;
        header = new StringList();
        key = new StringList();
        val = new StringList();
    }

    public int getLine() {
        return line;
    }

    public StringList getHeader() {
        return header;
    }

    public StringList getKey() {
        return key;
    }

    public StringList getVal() {
        return val;
    }
    
    public void createBackup() {
        backup = val.clone();
    }

    public void revert() {
        val = backup.clone();
    }

    public boolean isChanged() {
        return !val.equals(backup);
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
        buf.append(",key:");
        for (int i = 0; i < key.size(); i++) {
            buf.append("[");
            buf.append(i);
            buf.append("]\"");
            buf.append(key.get(i));
            buf.append("\"");
        }
        buf.append(",val:");
        for (int i = 0; i < val.size(); i++) {
            buf.append("[");
            buf.append(i);
            buf.append("]\"");
            buf.append(val.get(i));
            buf.append("\"");
        }
        buf.append("}");
        return buf.toString();
    }

    public TextReference[] getRef(String hint) {
        List<TextReference> list = new ArrayList<>();
        for (int index = 0; index < header.size(); index++) {
            String s = header.get(index);
            if (s.startsWith("#: ")) {
                for (String t : s.substring(3).split(" ")) {
                    String[] ss = t.split(":");
                    if (ss.length == 2 && !ss[0].isEmpty()) {
                        try {
                            String path = FileUtil.find(ss[0], hint);
                            if (path == null) {
                                path = ss[0];
                            }
                            int line = Integer.parseInt(ss[1]);
                            if (path != null && line > 0) {
                                list.add(new TextReference(path, line));
                            }
                        } catch (NumberFormatException e) {
                            // skip
                        }
                    }
                }
            }
        }
        return list.toArray(new TextReference[list.size()]);
    }

}
