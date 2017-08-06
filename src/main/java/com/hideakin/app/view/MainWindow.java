// Copyright (C) 2017 Hideaki Narita

package com.hideakin.app.view;

import java.io.File;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import com.hideakin.app.model.MainContext;
import com.hideakin.app.view.event.EditCompleteListener;
import com.hideakin.app.view.event.EditEvent;
import com.hideakin.app.view.layout.CustomLayout;
import com.hideakin.app.view.layout.CustomLayoutData;
import com.hideakin.app.view.menu.MenuBar;

public class MainWindow {

    private static final String TITLE_TAG_CHANGED = "[CHANGED] ";
    private static final String FORMAT_TITLE_PATH = "%s%s in %s";

    private Shell shell;
    private SearchBar searchBar;
    private EditableTable table;
    private FileView fileView;

    public MainWindow(Display display) {
        shell = new Shell(display);
        shell.setData(this);
        shell.setMenuBar(MenuBar.create(shell));
        shell.setLayout(new CustomLayout(SWT.VERTICAL));
        shell.addControlListener(new ControlListener() {
            @Override
            public void controlMoved(ControlEvent event) {
            }
            @Override
            public void controlResized(ControlEvent event) {
                shell.layout();
            }
        });

        searchBar = new SearchBar(shell);
        searchBar.setLayoutData(new CustomLayoutData(CustomLayoutData.TOP | CustomLayoutData.HFILL));
        searchBar.setVisible(false);

        fileView = new FileView(shell);
        fileView.setVisible(false);
        fileView.setLayoutData(new CustomLayoutData(CustomLayoutData.BOTTOM | CustomLayoutData.FILL));
 
        table = new EditableTable(shell);
        table.setEditCompleteListener(new EditCompleteListener() {
            @Override
            public void editComplete(EditEvent e) {
                if (e.document != null) {
                    setTitle(new File(e.document.getPath()), e.document.isChanged());
                } else {
                    setTitle(MainContext.APP_DISPLAY_NAME);
                }
            }
        });
        table.setLayoutData(new CustomLayoutData(CustomLayoutData.BOTTOM | CustomLayoutData.FILL));

        setTitle(MainContext.APP_DISPLAY_NAME);
    }

    public Shell getShell() {
        return shell;
    }

    public SearchBar getSearchBar() {
        return searchBar;
    }

    public EditableTable getTable() {
        return table;
    }

    public FileView getFiewView() {
        return fileView;
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

}
