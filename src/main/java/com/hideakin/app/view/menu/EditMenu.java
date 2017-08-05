// Copyright (C) 2017 Hideaki Narita

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
        
        MenuItem itemCopyKey = new MenuItem(menu, SWT.PUSH);
        itemCopyKey.setText("&Copy msgid");
        itemCopyKey.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                EventHandler.getInstance().copyKey();
            }
            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
                widgetSelected(e);
            }
        });

        MenuItem itemCopyVal = new MenuItem(menu, SWT.PUSH);
        itemCopyVal.setText("Copy msg&str");
        itemCopyVal.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                EventHandler.getInstance().copyVal();
            }
            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
                widgetSelected(e);
            }
        });

        new MenuItem(menu, SWT.SEPARATOR);

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
        itemCancelChange.setText("Ca&ncel edit");
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

        MenuItem itemRevertChange = new MenuItem(menu, SWT.PUSH);
        itemRevertChange.setText("&Revert edit");
        itemRevertChange.addSelectionListener(new SelectionListener() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                EventHandler.getInstance().revertEdit();
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
                widgetSelected(e);
            }
            
        });

        new MenuItem(menu, SWT.SEPARATOR);

        MenuItem itemFind = new MenuItem(menu, SWT.PUSH);
        itemFind.setText("&Find...");
        itemFind.addSelectionListener(new SelectionListener() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                EventHandler.getInstance().findView();
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
                boolean canCopy = EventHandler.getInstance().canCopy();
                itemCopyKey.setEnabled(canCopy);
                itemCopyVal.setEnabled(canCopy);
                if (EventHandler.getInstance().editInProgress()) {
                    itemChange.setText("&Leave edit mode");
                    itemChange.setEnabled(true);
                    itemCancelChange.setEnabled(true);
                    itemRevertChange.setEnabled(false);
                } else if (EventHandler.getInstance().canEnterEdit()) {
                    itemChange.setText("&Enter edit mode");
                    itemChange.setEnabled(true);
                    itemCancelChange.setEnabled(false);
                    itemRevertChange.setEnabled(true);
                } else {
                    itemChange.setText("&Enter edit mode");
                    itemChange.setEnabled(false);
                    itemCancelChange.setEnabled(false);
                    itemRevertChange.setEnabled(false);
                }
                itemFind.setEnabled(EventHandler.getInstance().canFind());
            }

        });

        return itemEdit;
    }

}
