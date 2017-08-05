// Copyright (C) 2017 Hideaki Narita

package com.hideakin.app.model;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class RecentlyUsedList {

    private static final Charset CHARSET = StandardCharsets.UTF_8;
    private static final String SEPARATOR = "\t";
    private static final String NEWLINE = System.lineSeparator();
    public static final int MAX_COUNT = 10;

    private String path;
    private List<String> list;
    private boolean loading;

    public RecentlyUsedList(String path) {
        this.path = path;
        this.list = new ArrayList<>();
        this.loading = false;
    }

    public List<String> getList() {
        return list;
    }

    public void load() {
        loading = true;
        try (BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(path), CHARSET))) {
            String text;
            while ((text = in.readLine()) != null) {
                String[] portions = text.split(SEPARATOR);
                if (portions.length == 2) {
                    File file = new File(portions[1]);
                    if (file.exists()) {
                        add(file.getCanonicalPath());
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        loading = false;
    }

    public void add(String path) {
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).equals(path)) {
                if (i > 0) {
                    list.remove(i);
                    list.add(0, path);
                    if (!loading) {
                        save(path);
                    }
                }
                return;
            }
        }
        list.add(0, path);
        if (list.size() > MAX_COUNT) {
            list.remove(MAX_COUNT);
        }
        if (!loading) {
            save(path);
        }
    }

    private void save(String entry) {
        try (BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(path, true), CHARSET))) {
            LocalDateTime dt = LocalDateTime.now();
            out.write(String.format("%d-%02d-%02d %02d:%02d:%02d",
                    dt.getYear(), dt.getMonthValue(), dt.getDayOfMonth(),
                    dt.getHour(), dt.getMinute(), dt.getSecond()));
            out.write(SEPARATOR);
            out.write(entry);
            out.write(NEWLINE);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
