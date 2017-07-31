// Copyright (C) 2017 Hideaki Narita

package com.hideakin.app.view.menu;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

import com.hideakin.app.controller.EventHandler;

public class ViewMenu {

    public static MenuItem create(Menu menubar) {
        MenuItem itemView = new MenuItem(menubar,SWT.CASCADE);
        itemView.setText("&View");

        Menu menu = new Menu(itemView);
        itemView.setMenu(menu);
        
        MenuItem itemFile = new MenuItem(menu, SWT.PUSH);
        itemFile.setText("&File");
        itemFile.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                EventHandler.getInstance().fileView();
            }
            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
                widgetSelected(e);
            }
        });

        return itemView;
    }

}
