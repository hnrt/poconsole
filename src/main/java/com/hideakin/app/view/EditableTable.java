/**
 * Copyright (C) 2017 Hideaki Narita
 */
package com.hideakin.app.view;

import java.util.ArrayList;
import java.util.Arrays;
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
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

import com.hideakin.app.model.TextReference;
import com.hideakin.app.model.TranslationDocument;
import com.hideakin.app.model.TranslationUnit;
import com.hideakin.app.view.event.EditCompleteListener;
import com.hideakin.app.view.event.EditEvent;
import com.hideakin.app.view.event.TranslationUnitSelectionEvent;
import com.hideakin.app.view.event.TranslationUnitSelectionListener;

public class EditableTable {

    private static final int STATUS_COLUMN = 0;
    private static final int LINE_COLUMN = 1;
    public static final int KEY_COLUMN = 2;
    public static final int VAL_COLUMN = 3;
    private static final String STATUS = "STATUS";
    private static final String LINE = "LINE";
    private static final String MSGID = "MSGID";
    private static final String MSGSTR = "MSGSTR";
    private static final String UNCHANGED = "";
    private static final String CHANGED = "CHANGED";
    private static final String EMPTY = "EMPTY";
    private static final String EDITING = "EDITING";
    private static final Color EMPTY_FGCOLOR = Display.getCurrent().getSystemColor(SWT.COLOR_YELLOW);
    private static final Color EMPTY_BGCOLOR = Display.getCurrent().getSystemColor(SWT.COLOR_DARK_MAGENTA);
    private static final Color EDITING_FGCOLOR = Display.getCurrent().getSystemColor(SWT.COLOR_WHITE);
    private static final Color EDITING_BGCOLOR = Display.getCurrent().getSystemColor(SWT.COLOR_RED);
    private static final Color CHANGED_FGCOLOR = Display.getCurrent().getSystemColor(SWT.COLOR_YELLOW);
    private static final Color CHANGED_BGCOLOR = Display.getCurrent().getSystemColor(SWT.COLOR_DARK_GREEN);
    private static final Color ODD_BGCOLOR = Display.getCurrent().getSystemColor(SWT.COLOR_WHITE);
    private static final Color EVEN_BGCOLOR = Display.getCurrent().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND);
    private static final boolean DEBUG_KBD = false;

    private Table table;
    private TableEditor tableEditor;
    private Text text;
    private TranslationDocument document;
    private EditCompleteListener editCompleteListener;
    private TranslationUnitSelectionListener tuSelectionListener;
    private boolean enterEditPending; // to work properly with menu navigation
    private boolean leaveEditPending; // to work properly with IME interaction
    private int selectedHeader;
    private boolean[] reverseOrder;

    public EditableTable(Composite parent) {
        table = new Table(parent, SWT.BORDER | SWT.FULL_SELECTION);

        selectedHeader = -1;
        reverseOrder = new boolean[4];
        Arrays.fill(reverseOrder, false);

        TableColumn colStatus = new TableColumn(table, SWT.LEFT);
        colStatus.setText(STATUS);
        colStatus.setWidth(100);
        colStatus.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetDefaultSelected(SelectionEvent arg0) {
                widgetSelected(arg0);
            }
            @Override
            public void widgetSelected(SelectionEvent arg0) {
                if (selectedHeader == STATUS_COLUMN) {
                    reverseOrder[STATUS_COLUMN] = !reverseOrder[STATUS_COLUMN];
                }
                EditableTable.this.sortByStatus(reverseOrder[STATUS_COLUMN]);
                selectedHeader = STATUS_COLUMN;
            }
        });
        TableColumn colLine = new TableColumn(table, SWT.LEFT);
        colLine.setText(LINE);
        colLine.setWidth(100);
        colLine.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetDefaultSelected(SelectionEvent arg0) {
                widgetSelected(arg0);
            }
            @Override
            public void widgetSelected(SelectionEvent arg0) {
                if (selectedHeader == LINE_COLUMN) {
                    reverseOrder[LINE_COLUMN] = !reverseOrder[LINE_COLUMN];
                }
                EditableTable.this.sortByLine(reverseOrder[LINE_COLUMN]);
                selectedHeader = LINE_COLUMN;
            }
        });
        TableColumn colId = new TableColumn(table, SWT.LEFT);
        colId.setText(MSGID);
        colId.setWidth(300);
        colId.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetDefaultSelected(SelectionEvent arg0) {
                widgetSelected(arg0);
            }
            @Override
            public void widgetSelected(SelectionEvent arg0) {
                if (selectedHeader == KEY_COLUMN) {
                    reverseOrder[KEY_COLUMN] = !reverseOrder[KEY_COLUMN];
                }
                EditableTable.this.sortById(reverseOrder[KEY_COLUMN]);
                selectedHeader = KEY_COLUMN;
            }
        });
        TableColumn colStr = new TableColumn(table, SWT.LEFT);
        colStr.setText(MSGSTR);
        colStr.setWidth(300);
        colStr.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetDefaultSelected(SelectionEvent arg0) {
                widgetSelected(arg0);
            }
            @Override
            public void widgetSelected(SelectionEvent arg0) {
                if (selectedHeader == VAL_COLUMN) {
                    reverseOrder[VAL_COLUMN] = !reverseOrder[VAL_COLUMN];
                }
                EditableTable.this.sortByStr(reverseOrder[VAL_COLUMN]);
                selectedHeader = VAL_COLUMN;
            }
        });

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
                if (e.item == null) {
                    return;
                }
                if (tuSelectionListener == null) {
                    return;
                }
                TranslationUnitSelectionEvent event = new TranslationUnitSelectionEvent();
                event.translateUnit = (TranslationUnit) e.item.getData();
                event.document = document;
                tuSelectionListener.translationUnitSelected(event);
            }
        });
    }

    public void setLayoutData(Object data) {
        table.setLayoutData(data);
    }

    public void setEditCompleteListener(EditCompleteListener listener) {
        editCompleteListener = listener;
    }

    public void setTranslationUnitSelectionListener(TranslationUnitSelectionListener listener) {
        tuSelectionListener = listener;
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
        text.setText(item.getText(VAL_COLUMN));
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
        tableEditor.setEditor(text, item, VAL_COLUMN);
        text.setFocus();
        text.selectAll();
        setStatusTextToEditing(item);
        leaveEditPending = false;
    }

    public void leaveEdit() {
        if (text == null) {
            return;
        }
        TableItem item = tableEditor.getItem();
        TranslationUnit tu = (TranslationUnit)item.getData();
        tu.getVal().set(text.getText());
        setStatusText(item, tu, table.indexOf(item));
        item.setText(VAL_COLUMN, tu.getVal().toString());
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
        setStatusText(item, tu, table.indexOf(item));
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
        setStatusText(item, tu, index);
        item.setText(VAL_COLUMN, tu.getVal().toString());
        if (editCompleteListener != null) {
            EditEvent e = new EditEvent();
            e.document = document;
            e.translateUnit = tu;
            editCompleteListener.editComplete(e);
        }
    }

    public TranslationDocument get() {
        return document;
    }

    public void set(TranslationDocument document) {
        table.setItemCount(0);
        List<TranslationUnit> tuList = document.getTranslationUnit();
        for (TranslationUnit tu : tuList) {
            TableItem item = new TableItem(table, SWT.NULL);
            setStatusText(item, tu, table.indexOf(item));
            item.setText(LINE_COLUMN, "" + (tu.getLine() + tu.getHeader().size()));
            item.setText(KEY_COLUMN, tu.getKey().toString());
            item.setText(VAL_COLUMN, tu.getVal().toString());
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
            setStatusText(item, tu, index);
        }
        if (editCompleteListener != null) {
            EditEvent e = new EditEvent();
            e.document = document;
            e.translateUnit = null;
            editCompleteListener.editComplete(e);
        }
    }

    private static void setStatusText(TableItem item, TranslationUnit tu, int index) {
        if (tu.getVal().isEmpty()) {
            item.setText(STATUS_COLUMN, EMPTY);
            item.setForeground(STATUS_COLUMN, EMPTY_FGCOLOR);
            item.setBackground(STATUS_COLUMN, EMPTY_BGCOLOR);
        } else if (tu.isChanged()) {
            item.setText(STATUS_COLUMN, CHANGED);
            item.setForeground(STATUS_COLUMN, CHANGED_FGCOLOR);
            item.setBackground(STATUS_COLUMN, CHANGED_BGCOLOR);
        } else {
            Color bgColor = (index & 1) == 0 ? ODD_BGCOLOR : EVEN_BGCOLOR;
            item.setText(STATUS_COLUMN, UNCHANGED);
            item.setForeground(STATUS_COLUMN, item.getForeground(LINE_COLUMN));
            item.setBackground(STATUS_COLUMN, bgColor);
        }
    }

    private static void setStatusTextToEditing(TableItem item) {
        item.setText(STATUS_COLUMN, EDITING);
        item.setForeground(STATUS_COLUMN, EDITING_FGCOLOR);
        item.setBackground(STATUS_COLUMN, EDITING_BGCOLOR);
    }

    public void sortByLine(boolean reverseOrder) {
        redisplay(document.getTranslationUnit(), reverseOrder);
    }

    public void sortByStatus(boolean reverseOrder) {
        List<TranslationUnit> tuList = document.getTranslationUnit();
        List<TranslationUnit> sorted = new ArrayList<>(tuList.size());
        for (TranslationUnit tu1 : tuList) {
            int x1 = tu1.getVal().isEmpty() ? 0 : tu1.isChanged() ? 1 : 2;
            for (int index = 0;; index++) {
                if (index >= sorted.size()) {
                    sorted.add(tu1);
                    break;
                }
                TranslationUnit tu2 = sorted.get(index); 
                int x2 = tu2.getVal().isEmpty() ? 0 : tu2.isChanged() ? 1 : 2;
                if (x1 < x2) {
                    sorted.add(index, tu1);
                    break;
                }
            }
        }
        redisplay(sorted, reverseOrder);
    }

    public void sortById(boolean reverseOrder) {
        List<TranslationUnit> tuList = document.getTranslationUnit();
        List<TranslationUnit> sorted = new ArrayList<>(tuList.size());
        for (TranslationUnit tu : tuList) {
            for (int index = 0;; index++) {
                if (index >= sorted.size()) {
                    sorted.add(tu);
                    break;
                } else if (tu.getKey().toString().compareTo(sorted.get(index).getKey().toString()) < 0) {
                    sorted.add(index, tu);
                    break;
                }
            }
        }
        redisplay(sorted, reverseOrder);
    }

    public void sortByStr(boolean reverseOrder) {
        List<TranslationUnit> tuList = document.getTranslationUnit();
        List<TranslationUnit> sorted = new ArrayList<>(tuList.size());
        for (TranslationUnit tu : tuList) {
            for (int index = 0;; index++) {
                if (index >= sorted.size()) {
                    sorted.add(tu);
                    break;
                } else if (tu.getVal().toString().compareTo(sorted.get(index).getVal().toString()) < 0) {
                    sorted.add(index, tu);
                    break;
                }
            }
        }
        redisplay(sorted, reverseOrder);
    }

    private void redisplay(List<TranslationUnit> tuList, boolean reverseOrder) {
        int index = reverseOrder ? tuList.size() - 1 : 0;
        for (TranslationUnit tu : tuList) {
            TableItem item = table.getItem(index);
            setStatusText(item, tu, index);
            item.setText(LINE_COLUMN, "" + (tu.getLine() + tu.getHeader().size()));
            item.setText(KEY_COLUMN, tu.getKey().toString());
            item.setText(VAL_COLUMN, tu.getVal().toString());
            item.setData(tu);
            index += reverseOrder ? -1 : 1;
        }
    }

    public boolean canFind() {
        return document != null && table.getItemCount() > 0;
    }

    public int find(String value, boolean caseSensitive, int subject, boolean forwardDirection) {
        if (value == null || value.isEmpty() || subject == 0 || document == null) {
            return -1;
        }
        final int itemCount = table.getItemCount();
        if (itemCount == 0) {
            return -1;
        }
        if (!caseSensitive) {
            value = value.toUpperCase();
        }
        int index;
        if (forwardDirection) {
            index = (table.getSelectionIndex() + 1) % itemCount;
        } else {
            index = table.getSelectionIndex();
            if (index < 0) {
                index = itemCount - 1;
            } else {
                index = (index + itemCount - 1) % itemCount;
            }
        }
        final boolean key = (subject & (1 << KEY_COLUMN)) != 0;
        final boolean val = (subject & (1 << VAL_COLUMN)) != 0;
        final int delta = forwardDirection ? 1 : (itemCount - 1);
        for (int count = itemCount; count > 0; count--) {
            TableItem item = table.getItem(index);
            TranslationUnit tu = (TranslationUnit)item.getData();
            if (caseSensitive) {
                if (key && tu.getKey().toString().contains(value) ||
                    val && tu.getVal().toString().contains(value)) {
                    table.setSelection(item);
                    return 1;
                }
            } else if (key && tu.getKey().toString().toUpperCase().contains(value) ||
                       val && tu.getVal().toString().toUpperCase().contains(value)) {
                table.setSelection(item);
                return 1;
            }
            index = (index + delta) % itemCount;
        }
        return 0;
    }

    public boolean hasSelection() {
        return table.getSelectionIndex() >= 0;
    }

    public String getSelectedKey() {
        int index = table.getSelectionIndex();
        if (index < 0) {
            return null;
        }
        TableItem item = table.getItem(index);
        TranslationUnit tu = (TranslationUnit)item.getData();
        return tu.getKey().toString();
    }

    public String getSelectedVal() {
        int index = table.getSelectionIndex();
        if (index < 0) {
            return null;
        }
        TableItem item = table.getItem(index);
        TranslationUnit tu = (TranslationUnit)item.getData();
        return tu.getVal().toString();
    }

    public TextReference[] getSelectedRef() {
        int index = table.getSelectionIndex();
        if (index < 0) {
            return null;
        }
        TableItem item = table.getItem(index);
        TranslationUnit tu = (TranslationUnit)item.getData();
        return tu.getRef(document.getPath());
    }

}
