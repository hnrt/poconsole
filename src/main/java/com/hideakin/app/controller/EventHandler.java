/**
 * Copyright (C) 2017 Hideaki Narita
 */
package com.hideakin.app.controller;

import java.io.File;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.events.ShellListener;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

import com.hideakin.app.exception.FileIoError;
import com.hideakin.app.model.MainContext;
import com.hideakin.app.model.TranslationDocument;
import com.hideakin.app.view.MainWindow;

public class EventHandler {

    private static EventHandler singleton;

    private MainContext mainContext;
    private MainWindow mainWindow;
    private boolean quitting;

    static {
        singleton = new EventHandler();
    }

    private EventHandler() {
        quitting = false;
    }

    public static EventHandler getInstance() {
        return singleton;
    }

    public void setMainContext(MainContext ctx) {
        mainContext = ctx;
    }

    public void setMainWindow(MainWindow win) {
        mainWindow = win;
        Shell shell = mainWindow.getShell();
        shell.addShellListener(new ShellListener() {
            @Override
            public void shellActivated(ShellEvent e) {
            }
            @Override
            public void shellClosed(ShellEvent e) {
                if (!quitting) {
                    quitting = true;
                    quit();
                }
            }
            @Override
            public void shellDeactivated(ShellEvent e) {
            }
            @Override
            public void shellDeiconified(ShellEvent e) {
            }
            @Override
            public void shellIconified(ShellEvent e) {
            }
        });
    }

    public void parseCommandLine(String[] args) {
        String path = null;
        for (String arg : args) {
            if (path == null) {
                path = arg;
            } else {
                System.err.println("Error: Too many command line parameters.");
                System.exit(1);
            }
        }
        if (path != null) {
            openFile(path, false);
        }
    }

    public void quit() {
        int response = askToSave();
        if (response == SWT.YES) {
            saveFile();
        } else if (response == SWT.CANCEL) {
            return;
        }
        if (mainContext.quit() && !quitting) {
            quitting = true;
            mainWindow.close();
        }
    }

    public void openFile() {
        int response = askToSave();
        if (response == SWT.YES) {
            saveFile();
        } else if (response == SWT.CANCEL) {
            return;
        }
        FileDialog dialog = new FileDialog(mainWindow.getShell(), SWT.OPEN);
        dialog.setText("Choose a file to open");
        dialog.setFilterExtensions(new String[] {"*.po", "*.*"});
        String path = dialog.open();
        if (path == null) {
            return;
        }
        openFile(path, false);
    }

    public void openFile(String path, boolean check) {
        if (check) {
            int response = askToSave();
            if (response == SWT.YES) {
                saveFile();
            } else if (response == SWT.CANCEL) {
                return;
            }
        }
        try {
            TranslationDocument document = mainContext.open(path);
            mainWindow.getTable().set(document);
        } catch (FileIoError error) {
            MessageBox box = new MessageBox(mainWindow.getShell(), SWT.ICON_ERROR | SWT.OK);
            box.setText("ERROR");
            box.setMessage(error.getMessage() + "\n\n" + error.getPath());
            box.open();
        }
    }

    private int askToSave() {
        TranslationDocument document = mainContext.getDocument();
        if (document != null && document.isChanged()) {
            MessageBox box = new MessageBox(mainWindow.getShell(), SWT.ICON_QUESTION | SWT.YES | SWT.NO | SWT.CANCEL);
            box.setText("CONFIRM");
            box.setMessage("Document has been changed. Do you wish to save it?");
            return box.open();
        } else {
            return SWT.NO;
        }
    }

    public void saveFile() {
        try {
            TranslationDocument document = mainContext.save();
            document.createBackup();
            mainWindow.getTable().updateStatus();
        } catch (FileIoError error) {
            MessageBox box = new MessageBox(mainWindow.getShell(), SWT.ICON_ERROR | SWT.OK);
            box.setText("ERROR");
            box.setMessage(error.getMessage() + "\n\n" + error.getPath());
            box.open();
        }
    }

    public void saveFileAs() {
        TranslationDocument document = mainContext.getDocument();
        if (document == null) {
            return;
        }
        FileDialog dialog = new FileDialog(mainWindow.getShell(), SWT.SAVE);
        dialog.setText("Choose a file to save");
        dialog.setFilterExtensions(new String[] {"*.po", "*.*"});
        String path = dialog.open();
        if (path == null) {
            return;
        }
        if (document.getPath().equals(path)) {
            MessageBox box = new MessageBox(mainWindow.getShell(), SWT.ICON_QUESTION | SWT.YES | SWT.NO);
            box.setText("CONFIRM");
            box.setMessage("Path is the same as the current document. Do you wish to save it anyway?");
            int response = box.open();
            if (response == SWT.YES) {
                saveFile();
            }
            return;
        }
        File file = new File(path);
        if (file.exists()) {
            MessageBox box = new MessageBox(mainWindow.getShell(), SWT.ICON_QUESTION | SWT.YES | SWT.NO);
            box.setText("CONFIRM");
            box.setMessage("File already exists. Do you wish to save the document to this path anyway?\n\n" + path);
            int response = box.open();
            if (response == SWT.NO) {
                return;
            }
            MessageBox box2 = new MessageBox(mainWindow.getShell(), SWT.ICON_WARNING | SWT.YES | SWT.NO);
            box2.setText("RECONFIRM");
            box2.setMessage("Are you sure to save the document to this path?\n\n" + path);
            int response2 = box2.open();
            if (response2 == SWT.NO) {
                return;
            }
        }
        try {
            mainContext.save(path);
            mainWindow.getTable().updateStatus();
        } catch (FileIoError error) {
            MessageBox box = new MessageBox(mainWindow.getShell(), SWT.ICON_ERROR | SWT.OK);
            box.setText("ERROR");
            box.setMessage(error.getMessage() + "\n\n" + error.getPath());
            box.open();
            return;
        }
        openFile(path, false);
    }


    public boolean editInProgress() {
        return mainWindow.getTable().editInProgress();
    }

    public boolean canEnterEdit() {
        return mainWindow.getTable().canEnterEdit();
    }

    public void enterEdit() {
        mainWindow.getTable().enterEdit();
    }

    public void leaveEdit() {
        mainWindow.getTable().leaveEdit();
    }

    public void cancelEdit() {
        mainWindow.getTable().cancelEdit();
    }

    public void revertEdit() {
        mainWindow.getTable().revertEdit();
    }

}
