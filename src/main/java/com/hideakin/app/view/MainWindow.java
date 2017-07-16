/**
 * Copyright (C) 2017 Hideaki Narita
 */
package com.hideakin.app.view;

import java.io.File;

import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import com.hideakin.app.view.event.EditCompleteListener;
import com.hideakin.app.view.event.EditEvent;
import com.hideakin.app.view.menu.MenuBar;

public class MainWindow {

    private static final String DEFAULT_TITLE = "gettext Portable Object Console";
    private static final String TITLE_TAG_CHANGED = "[CHANGED] ";
    private static final String FORMAT_TITLE_PATH = "%s%s in %s";

    private Shell shell;
    private EditableTable table;

    public MainWindow(Display display) {
        shell = new Shell(display);
        shell.setData(this);
        shell.setMenuBar(MenuBar.create(shell));
        shell.setLayout(new FillLayout());
        table = new EditableTable(shell);
        table.setEditCompleteListener(new EditCompleteListener() {
            @Override
            public void editComplete(EditEvent e) {
                setTitle(new File(e.document.getPath()), e.document.isChanged());
            }
        });
        setTitle(DEFAULT_TITLE);
    }

    public Shell getShell() {
        return shell;
    }

    public EditableTable getTable() {
        return table;
    }

    public void close() {
        shell.close();
    }

    public void setTitle(String text) {
        shell.setText(text);
    }

    public void setTitle(File file, boolean changed) {
        shell.setText(
                String.format(FORMAT_TITLE_PATH,
                    changed ? TITLE_TAG_CHANGED : "",
                    file.getName(),
                    file.getParent()));
    }

    public boolean editInProgress() {
        return table.editInProgress();
    }

    public boolean canEnterEdit() {
        return table.canEnterEdit();
    }

    public void enterEdit() {
        table.enterEdit();
    }

    public void leaveEdit() {
        table.leaveEdit();
    }

    public void cancelEdit() {
        table.cancelEdit();
    }

}
