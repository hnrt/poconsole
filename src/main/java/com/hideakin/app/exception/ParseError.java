// Copyright (C) 2017 Hideaki Narita

package com.hideakin.app.exception;

public class ParseError extends Exception {

    private static final long serialVersionUID = 1L;

    private int line;

    public ParseError(int line, String message) {
        super(message);
        this.line = line;
    }

    public int getLine() {
        return line;
    }

}
