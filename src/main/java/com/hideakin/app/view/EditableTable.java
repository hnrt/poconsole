/**
 * Copyright (C) 2017 Hideaki Narita
 */
package com.hideakin.app.view;

import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import com.hideakin.app.model.TranslationDocument;
import com.hideakin.app.model.TranslationUnit;
import com.hideakin.app.view.event.EditCompleteListener;
import com.hideakin.app.view.event.EditEvent;

public class EditableTable {

    private static final int STATUS_COLUMN = 0;
    private static final int LINE_COLUMN = 1;
    private static final int ID_COLUMN = 2;
    private static final int EDIT_COLUMN = 3;
    private static final String STATUS = "STATUS";
    private static final String LINE = "LINE";
    private static final String MSGID = "MSGID";
    private static final String MSGSTR = "MSGSTR";
    private static final String UNCHANGED = "";
    private static final String CHANGED = "CHANGED";
    private static final String EMPTY = "EMPTY";
    private static final String EDITING = "EDITING";
    private static final boolean DEBUG_KBD = false;

    private Table table;
    private TableEditor tableEditor;
    private Text text;
    private TranslationDocument document;
    private EditCompleteListener editCompleteListener;
    private boolean enterEditPending; // to work properly with menu navigation
    private boolean leaveEditPending; // to work properly with IME interaction

    public EditableTable(Composite parent) {
        table = new Table(parent, SWT.BORDER | SWT.FULL_SELECTION);

        TableColumn col;
        col = new TableColumn(table, SWT.LEFT);
        col.setText(STATUS);
        col.setWidth(100);
        col = new TableColumn(table, SWT.LEFT);
        col.setText(LINE);
        col.setWidth(100);
        col = new TableColumn(table, SWT.LEFT);
        col.setText(MSGID);
        col.setWidth(300);
        col = new TableColumn(table, SWT.LEFT);
        col.setText(MSGSTR);
        col.setWidth(300);

        table.setHeaderVisible(true);
        table.setLinesVisible(true);
        
        tableEditor = new TableEditor(table);
        tableEditor.grabHorizontal = true;

        table.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                enterEditPending = false;
                leaveEditPending = false;
            }
        });

        table.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.keyCode == SWT.CR && (e.stateMask & (SWT.CTRL | SWT.ALT | SWT.SHIFT)) == 0) {
                    enterEditPending = true;
                }
            }
            @Override
            public void keyReleased(KeyEvent e) {
                if (e.keyCode == SWT.CR && (e.stateMask & (SWT.CTRL | SWT.ALT | SWT.SHIFT)) == 0) {
                    if (enterEditPending) {
                        enterEdit();
                    }
                }
                enterEditPending = false;
            }
        });

        table.addMouseListener(new MouseListener() {
            @Override
            public void mouseDoubleClick(MouseEvent e) {
                enterEdit();
            }
            @Override
            public void mouseDown(MouseEvent e) {
            }
            @Override
            public void mouseUp(MouseEvent e) {
            }
        });

        table.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
            }
        });
    }

    public void setEditCompleteListener(EditCompleteListener listener) {
        editCompleteListener = listener;
    }

    public boolean editInProgress() {
        return text != null;
    }

    public boolean canEnterEdit() {
        return text == null && table.getSelectionIndex() >= 0;
    }

    public void enterEdit() {
        int index = table.getSelectionIndex();
        if (index == -1) {
            return;
        }
        table.setSelection(new int[0]);
        TableItem item = table.getItem(index);
        text = new Text(table, SWT.MULTI);
        text.setText(item.getText(EDIT_COLUMN));
        text.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                leaveEdit();
            }
        });
        text.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (DEBUG_KBD) {
                    System.out.printf(" pressed: code=%x mask=%x char=%x\n",
                            e.keyCode, e.stateMask, (int)e.character);
                }
                // Observation of Anthy Input Method on CentOS 6.9
                // Conversion of a single character completed by ENTER yields a single keyPressd with keyCode=CR and character=actual.
                // Conversion of multiple characters completed by ENTER yields multiple keyPressed with keyCode=0 and character=actual.
                // In both cases, a single keyReleased follows with keyCode=CR and character=CR.
                // Note: Shell.getImeInputMode() always returns 0, which means it doesn't look to be working correctly.
                if (e.keyCode == SWT.CR && (e.stateMask & (SWT.CTRL | SWT.ALT | SWT.SHIFT)) == 0 && e.character == '\r') {
                    e.doit = false;
                    leaveEditPending = true;
                }
            }
            @Override
            public void keyReleased(KeyEvent e) {
                if (DEBUG_KBD) {
                    System.out.printf("released: code=%x mask=%x char=%x\n",
                            e.keyCode, e.stateMask, (int)e.character);
                }
                if (leaveEditPending) {
                    leaveEditPending = false;
                    if (e.keyCode == SWT.CR && (e.stateMask & (SWT.CTRL | SWT.ALT | SWT.SHIFT)) == 0) {
                        e.doit = false;
                        leaveEdit();
                    }
                } else if (e.keyCode == SWT.ESC) {
                    cancelEdit();
                }
            }
        });
        text.addMouseListener(new MouseListener() {
            @Override
            public void mouseDoubleClick(MouseEvent e) {
                leaveEdit();
            }
            @Override
            public void mouseDown(MouseEvent e) {
            }
            @Override
            public void mouseUp(MouseEvent e) {
            }
        });
        tableEditor.setEditor(text, item, EDIT_COLUMN);
        text.setFocus();
        text.selectAll();
        item.setText(STATUS_COLUMN, EDITING);
        leaveEditPending = false;
    }

    public void leaveEdit() {
        if (text == null) {
            return;
        }
        TableItem item = tableEditor.getItem();
        TranslationUnit tu = (TranslationUnit)item.getData();
        tu.getStr().set(text.getText());
        item.setText(STATUS_COLUMN, tu.getStr().isEmpty() ? EMPTY : tu.isChanged() ? CHANGED : UNCHANGED);
        item.setText(EDIT_COLUMN, tu.getStr().toString());
        text.dispose();
        text = null;
        table.setSelection(item);
        if (editCompleteListener != null) {
            EditEvent e = new EditEvent();
            e.document = document;
            e.translateUnit = tu;
            editCompleteListener.editComplete(e);
        }
    }

    public void cancelEdit() {
        if (text == null) {
            return;
        }
        TableItem item = tableEditor.getItem();
        TranslationUnit tu = (TranslationUnit)item.getData();
        item.setText(STATUS_COLUMN, tu.getStr().isEmpty() ? EMPTY : tu.isChanged() ? CHANGED : UNCHANGED);
        text.dispose();
        text = null;
        table.setSelection(item);
    }

    public void revertEdit() {
        if (text != null) {
            return;
        }
        int index = table.getSelectionIndex();
        if (index == -1) {
            return;
        }
        TableItem item = table.getItem(index);
        TranslationUnit tu = (TranslationUnit)item.getData();
        tu.revert();
        item.setText(STATUS_COLUMN, tu.getStr().isEmpty() ? EMPTY : UNCHANGED);
        item.setText(EDIT_COLUMN, tu.getStr().toString());
        if (editCompleteListener != null) {
            EditEvent e = new EditEvent();
            e.document = document;
            e.translateUnit = tu;
            editCompleteListener.editComplete(e);
        }
    }

    public void set(TranslationDocument document) {
        table.setItemCount(0);
        List<TranslationUnit> tuList = document.getTranslationUnit();
        for (TranslationUnit tu : tuList) {
            TableItem item = new TableItem(table, SWT.NULL);
            item.setText(STATUS_COLUMN, tu.getStr().isEmpty() ? EMPTY : UNCHANGED);
            item.setText(LINE_COLUMN, "" + tu.getLine() + tu.getHeader().size());
            item.setText(ID_COLUMN, tu.getId().toString());
            item.setText(EDIT_COLUMN, tu.getStr().toString());
            item.setData(tu);
        }
        this.document = document;
        if (editCompleteListener != null) {
            EditEvent e = new EditEvent();
            e.document = document;
            e.translateUnit = null;
            editCompleteListener.editComplete(e);
        }
    }

    public void updateStatus() {
        int count = table.getItemCount();
        for (int index = 0; index < count; index++) {
            TableItem item = table.getItem(index);
            TranslationUnit tu = (TranslationUnit)item.getData();
            item.setText(STATUS_COLUMN, tu.getStr().isEmpty() ? EMPTY : tu.isChanged() ? CHANGED : UNCHANGED);
        }
        if (editCompleteListener != null) {
            EditEvent e = new EditEvent();
            e.document = document;
            e.translateUnit = null;
            editCompleteListener.editComplete(e);
        }
    }

}
