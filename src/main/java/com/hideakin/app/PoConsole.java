// Copyright (C) 2017 Hideaki Narita

package com.hideakin.app;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import com.hideakin.app.controller.EventHandler;
import com.hideakin.app.model.MainContext;
import com.hideakin.app.view.MainWindow;

/**
 * GetText Portable Object Console class 
 */
public class PoConsole {

    /**
     * @param args
     */
    public static void main(String[] args) {
        MainContext mainContext = MainContext.getInstance();
        mainContext.init();
        Display display = new Display();
        MainWindow mainWindow = new MainWindow(display);
        EventHandler eventHandler = EventHandler.getInstance();
        eventHandler.setMainContext(mainContext);
        eventHandler.setMainWindow(mainWindow);
        eventHandler.parseCommandLine(args);
        Shell shell = mainWindow.getShell();
        shell.open();
        while (!shell.isDisposed()) {
            if (!display.readAndDispatch()) {
                display.sleep();
            }
        }
        display.dispose();
    }

}
