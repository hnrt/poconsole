// Copyright (C) 2017 Hideaki Narita

package com.hideakin.app.model;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RecentlyUsedList {

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
        try (OperationLogReader in = OperationLog.getReader(path)) {
            Operation entry;
            while ((entry = in.read()) != null) {
                if (entry.getKind() == OperationKind.LOAD) {
                    File file = new File(entry.getPath());
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
        try (OperationLogWriter out = OperationLog.getWriter(path)) {
            out.write(OperationKind.LOAD, entry);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
