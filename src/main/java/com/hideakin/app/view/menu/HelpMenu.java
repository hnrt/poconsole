// Copyright (C) 2017 Hideaki Narita

package com.hideakin.app.view.menu;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

import com.hideakin.app.controller.EventHandler;

public class HelpMenu {

    public static MenuItem create(Menu menubar) {
        MenuItem itemView = new MenuItem(menubar,SWT.CASCADE);
        itemView.setText("&Help");

        Menu menu = new Menu(itemView);
        itemView.setMenu(menu);
        
        MenuItem itemAbout = new MenuItem(menu, SWT.PUSH);
        itemAbout.setText("&About");
        itemAbout.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                EventHandler.getInstance().about();
            }
            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
                widgetSelected(e);
            }
        });

        return itemView;
    }

}
