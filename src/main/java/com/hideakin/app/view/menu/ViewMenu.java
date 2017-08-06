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
import com.hideakin.app.view.MainWindow;

public class ViewMenu {

    public static MenuItem create(Menu menubar) {
        MenuItem itemView = new MenuItem(menubar,SWT.CASCADE);
        itemView.setText("&View");

        Menu menu = new Menu(itemView);
        itemView.setMenu(menu);
        
        MenuItem itemFind = new MenuItem(menu, SWT.CHECK);
        itemFind.setText("&Find");
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

        MenuItem itemFile = new MenuItem(menu, SWT.CHECK);
        itemFile.setText("F&ile");
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

        new MenuItem(menu, SWT.SEPARATOR);

        MenuItem itemRefresh = new MenuItem(menu, SWT.PUSH);
        itemRefresh.setText("&Refresh");
        itemRefresh.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                EventHandler.getInstance().refresh();
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
                MainWindow win = (MainWindow) menubar.getData();
                itemFind.setSelection(win.getSearchBar().getVisible());
                itemFile.setSelection(win.getFiewView().getVisible());
            }
        });

        return itemView;
    }

}
