/**
 * Copyright (C) 2017 Hideaki Narita
 */
package com.hideakin.app.view;

import java.io.File;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import com.hideakin.app.view.event.EditCompleteListener;
import com.hideakin.app.view.event.EditEvent;
import com.hideakin.app.view.layout.CustomLayout;
import com.hideakin.app.view.layout.CustomLayoutData;
import com.hideakin.app.view.menu.MenuBar;

public class MainWindow {

    private static final String DEFAULT_TITLE = "gettext Portable Object Console";
    private static final String TITLE_TAG_CHANGED = "[CHANGED] ";
    private static final String FORMAT_TITLE_PATH = "%s%s in %s";

    private Shell shell;
    private SearchBar searchBar;
    private EditableTable table;

    public MainWindow(Display display) {
        shell = new Shell(display);
        shell.setData(this);
        shell.setMenuBar(MenuBar.create(shell));
        shell.setLayout(new CustomLayout(SWT.VERTICAL));
        shell.addControlListener(new ControlListener() {
            @Override
            public void controlMoved(ControlEvent arg0) {
            }
            @Override
            public void controlResized(ControlEvent arg0) {
                shell.layout();
            }
        });

        searchBar = new SearchBar(shell);
        searchBar.setLayoutData(new CustomLayoutData(CustomLayoutData.TOP | CustomLayoutData.HFILL));
        searchBar.setVisible(false);

        table = new EditableTable(shell);
        table.setEditCompleteListener(new EditCompleteListener() {
            @Override
            public void editComplete(EditEvent e) {
                setTitle(new File(e.document.getPath()), e.document.isChanged());
            }
        });
        table.setLayoutData(new CustomLayoutData(CustomLayoutData.BOTTOM | CustomLayoutData.FILL));

        setTitle(DEFAULT_TITLE);
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
