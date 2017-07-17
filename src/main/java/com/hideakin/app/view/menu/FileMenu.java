/**
 * Copyright (C) 2017 Hideaki Narita
 */
package com.hideakin.app.view.menu;

import java.io.File;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MenuEvent;
import org.eclipse.swt.events.MenuListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

import com.hideakin.app.controller.EventHandler;
import com.hideakin.app.model.MainContext;

public class FileMenu {

    public static MenuItem create(Menu menubar) {
        MenuItem itemFile = new MenuItem(menubar,SWT.CASCADE);
        itemFile.setText("&File");
        
        Menu menu = new Menu(itemFile);
        itemFile.setMenu(menu);
        
        MenuItem itemOpen = new MenuItem(menu, SWT.PUSH);
        itemOpen.setText("&Open...");
        itemOpen.addSelectionListener(new SelectionListener() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                EventHandler.getInstance().openFile();
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
                widgetSelected(e);
            }
            
        });

        MenuItem itemSave = new MenuItem(menu, SWT.PUSH);
        itemSave.setText("&Save");
        itemSave.addSelectionListener(new SelectionListener() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                EventHandler.getInstance().saveFile();
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
                widgetSelected(e);
            }
            
        });

        MenuItem itemSaveAs = new MenuItem(menu, SWT.PUSH);
        itemSaveAs.setText("Save &as...");
        itemSaveAs.addSelectionListener(new SelectionListener() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                EventHandler.getInstance().saveFileAs();
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
                widgetSelected(e);
            }
            
        });

        new MenuItem(menu, SWT.SEPARATOR);

        MenuItem itemRecent = new MenuItem(menu,SWT.CASCADE);
        itemRecent.setText("&Recently used");
        Menu menuRecent = new Menu(itemRecent);
        menuRecent.addMenuListener(new MenuListener() {

            @Override
            public void menuHidden(MenuEvent e) {
            }

            @Override
            public void menuShown(MenuEvent e) {
                for (MenuItem item : menuRecent.getItems()) {
                    item.dispose();
                }
                List<String> ru = MainContext.getInstance().getRecentlyUsed();
                if (ru.size() == 0) {
                    MenuItem item = new MenuItem(menuRecent, SWT.PUSH);
                    item.setText("No file");
                    item.setEnabled(false);
                    return;                    
                }
                String documentPath = MainContext.getInstance().getDocumentPath();
                for (String path : ru) {
                    if (path.equals(documentPath)) {
                        continue;
                    }
                    File file = new File(path);
                    String parent = file.getParent();
                    MenuItem item = new MenuItem(menuRecent, SWT.PUSH);
                    item.setText(file.getName() + " (" + parent + ")");
                    item.addSelectionListener(new SelectionListener() {

                        @Override
                        public void widgetSelected(SelectionEvent e) {
                            EventHandler.getInstance().openFile(path, true);
                        }

                        @Override
                        public void widgetDefaultSelected(SelectionEvent e) {
                            widgetSelected(e);
                        }
                        
                    });
                }
            }

        });
        itemRecent.setMenu(menuRecent);

        new MenuItem(menu, SWT.SEPARATOR);

        MenuItem itemQuit = new MenuItem(menu, SWT.PUSH);
        itemQuit.setText("&Quit");
        itemQuit.addSelectionListener(new SelectionListener() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                EventHandler.getInstance().quit();
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
                boolean canSave = MainContext.getInstance().getDocument() != null;
                itemSave.setEnabled(canSave);
                itemSaveAs.setEnabled(canSave);
            }

        });

        return itemFile;        
    }

}
