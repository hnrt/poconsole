// Copyright (C) 2017 Hideaki Narita

package com.hideakin.app.view.menu;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;

public class MenuBar {

    public static Menu create(Shell shell) {
        Menu menubar = new Menu(shell, SWT.BAR);
        menubar.setData(shell.getData());
        FileMenu.create(menubar);
        EditMenu.create(menubar);
        ViewMenu.create(menubar);
        HelpMenu.create(menubar);
        return menubar;
    }

}
