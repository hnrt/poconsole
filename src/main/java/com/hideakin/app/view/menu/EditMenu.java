/**
 * Copyright (C) 2017 Hideaki Narita
 */
package com.hideakin.app.view.menu;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MenuEvent;
import org.eclipse.swt.events.MenuListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

import com.hideakin.app.controller.EventHandler;

public class EditMenu {

    public static MenuItem create(Menu menubar) {
        MenuItem itemEdit = new MenuItem(menubar,SWT.CASCADE);
        itemEdit.setText("&Edit");

        Menu menu = new Menu(itemEdit);
        itemEdit.setMenu(menu);
        
        MenuItem itemChange = new MenuItem(menu, SWT.PUSH);
        itemChange.addSelectionListener(new SelectionListener() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                if (EventHandler.getInstance().editInProgress()) {
                    EventHandler.getInstance().leaveEdit();
                } else {
                    EventHandler.getInstance().enterEdit();
                }
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
                widgetSelected(e);
            }
            
        });

        MenuItem itemCancelChange = new MenuItem(menu, SWT.PUSH);
        itemCancelChange.setText("&Cancel edit");
        itemCancelChange.addSelectionListener(new SelectionListener() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                EventHandler.getInstance().cancelEdit();
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
                widgetSelected(e);
            }
            
        });

        menu.addMenuListener(new MenuListener() {
            
            @Override
            public void menuHidden(MenuEvent e) {
            }

            @Override
            public void menuShown(MenuEvent e) {
                if (EventHandler.getInstance().editInProgress()) {
                    itemChange.setText("&Leave edit mode");
                    itemChange.setEnabled(true);
                    itemCancelChange.setEnabled(true);
                } else if (EventHandler.getInstance().canEnterEdit()) {
                    itemChange.setText("&Enter edit mode");
                    itemChange.setEnabled(true);
                    itemCancelChange.setEnabled(false);
                } else {
                    itemChange.setText("&Enter edit mode");
                    itemChange.setEnabled(false);
                    itemCancelChange.setEnabled(false);
                }
            }

        });

        return itemEdit;
    }

}
