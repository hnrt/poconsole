// Copyright (C) 2017 Hideaki Narita

package com.hideakin.app.controller;

import java.io.File;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.events.ShellListener;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.MessageBox;
import com.hideakin.app.exception.FileIoError;
import com.hideakin.app.model.MainContext;
import com.hideakin.app.model.TranslationDocument;
import com.hideakin.app.view.EditableTable;
import com.hideakin.app.view.FileView;
import com.hideakin.app.view.MainWindow;
import com.hideakin.app.view.event.TranslationUnitSelectionEvent;
import com.hideakin.app.view.event.TranslationUnitSelectionListener;

public class EventHandler {

    public static final int KEY = 1 << EditableTable.KEY_COLUMN;
    public static final int VAL = 1 << EditableTable.VAL_COLUMN;

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

        mainWindow.getShell().addShellListener(new ShellListener() {
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

        mainWindow.getTable().setTranslationUnitSelectionListener(new TranslationUnitSelectionListener() {
            @Override
            public void translationUnitSelected(TranslationUnitSelectionEvent event) {
                FileView fv = mainWindow.getFiewView();
                if (fv.getVisible()) {
                    fv.set(event.translateUnit.getRef(event.document.getPath()));
                }
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

    public boolean canCopy() {
        return mainWindow.getTable().hasSelection();
    }

    public void copyKey() {
        String key = mainWindow.getTable().getSelectedKey();
        if (key == null) {
            return;
        }
        Clipboard clipboard = new Clipboard(Display.getCurrent());
        TextTransfer textTransfer = TextTransfer.getInstance();
        Transfer[] transfers = new Transfer[] { textTransfer };
        Object[] data = new Object[] { key };
        clipboard.setContents(data, transfers, DND.CLIPBOARD);
        clipboard.dispose();
    }

    public void copyVal() {
        String val = mainWindow.getTable().getSelectedVal();
        if (val == null) {
            return;
        }
        Clipboard clipboard = new Clipboard(Display.getCurrent());
        TextTransfer textTransfer = TextTransfer.getInstance();
        Transfer[] transfers = new Transfer[] { textTransfer };
        Object[] data = new Object[] { val };
        clipboard.setContents(data, transfers, DND.CLIPBOARD);
        clipboard.dispose();
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

    public boolean canFind() {
        return mainWindow.getTable().canFind();
    }

    public void findView() {
        mainWindow.getSearchBar().setVisible(!mainWindow.getSearchBar().getVisible());
        mainWindow.getShell().layout();
    }

    public int find(String value, boolean caseSensitive, int subject, boolean forwardDirection) {
        return mainWindow.getTable().find(value, caseSensitive, subject, forwardDirection);
    }

    public void fileView() {
        FileView fv = mainWindow.getFiewView();
        boolean visible = !fv.getVisible();
        fv.setVisible(visible);
        mainWindow.getShell().layout();
        fv.set(visible ? mainWindow.getTable().getSelectedRef() : null);
    }

    public void refresh() {
        mainWindow.getShell().layout();
    }

    public void about() {
        MessageBox box = new MessageBox(mainWindow.getShell(), SWT.OK);
        box.setText("About");
        StringBuilder sb = new StringBuilder();
        sb.append(MainContext.APP_DISPLAY_NAME);
        sb.append("\n");
        sb.append(MainContext.APP_VERSION);
        sb.append("\n");
        sb.append(MainContext.COPYRIGHT);
        box.setMessage(sb.toString());
        box.open();
    }

}
