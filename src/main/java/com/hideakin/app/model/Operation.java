// Copyright (C) 2017 Hideaki Narita

package com.hideakin.app.model;

public class Operation {

    private OperationKind kind;
    private String path;

    public Operation(String kind, String path) {
        this.kind = OperationKind.valueOf(kind);
        this.path = path;
    }

    public OperationKind getKind() {
        return kind;
    }

    public String getPath() {
        return path;
    }

}
