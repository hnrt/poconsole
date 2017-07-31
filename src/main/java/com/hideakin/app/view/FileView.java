// Copyright (C) 2017 Hideaki Narita

package com.hideakin.app.view;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

import com.hideakin.app.model.TextFile;
import com.hideakin.app.model.TextReference;
import com.hideakin.app.view.layout.CustomLayout;
import com.hideakin.app.view.layout.CustomLayoutData;

public class FileView extends Composite {

    private static final String LINE = "LINE";
    private static final String CONTENTS = "CONTENTS";

    private Table table;
    private Combo combo;
    private TextFile textFile;
    private TextReference[] entries;

    public FileView(Composite parent) {
        super(parent, SWT.NULL);

        CustomLayout layout = new CustomLayout(SWT.VERTICAL);
        this.setLayout(layout);

        Composite header = new Composite(this, SWT.NULL);
        header.setLayout(new CustomLayout(SWT.HORIZONTAL));
        header.setLayoutData(new CustomLayoutData(CustomLayoutData.HFILL | CustomLayoutData.TOP));

        combo = new Combo(header, SWT.READ_ONLY);
        combo.setLayoutData(new CustomLayoutData(CustomLayoutData.LEFT | CustomLayoutData.VCENTER));
        combo.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetDefaultSelected(SelectionEvent event) {
                widgetSelected(event);
            }
            @Override
            public void widgetSelected(SelectionEvent event) {
                int index = combo.getSelectionIndex();
                load(entries[index]);
            }
        });
        
        Button closeButton = new Button(header, SWT.PUSH);
        closeButton.setText("\u2613");
        closeButton.setToolTipText("Hide");
        closeButton.setLayoutData(new CustomLayoutData(CustomLayoutData.RIGHT | CustomLayoutData.VCENTER));
        ButtonUtil.adjustSize(closeButton);

        closeButton.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetDefaultSelected(SelectionEvent event) {
                widgetSelected(event);
            }
            @Override
            public void widgetSelected(SelectionEvent event) {
                FileView.this.setVisible(false);
                parent.layout();
            }
        });

        table = new Table(this, SWT.BORDER | SWT.FULL_SELECTION);
        table.setLayoutData(new CustomLayoutData(CustomLayoutData.HFILL | CustomLayoutData.BOTTOM | CustomLayoutData.VFILL));

        TableColumn colLine = new TableColumn(table, SWT.LEFT);
        colLine.setText(LINE);
        colLine.setWidth(100);

        TableColumn colText = new TableColumn(table, SWT.LEFT);
        colText.setText(CONTENTS);
        colText.setWidth(400);

        table.setHeaderVisible(true);
        table.setLinesVisible(true);
    }

    public void parse(TextReference[] entries) {
        combo.removeAll();
        this.entries = entries;
        if (entries == null || entries.length == 0) {
            table.setItemCount(0);
            textFile = null;
            return;
        }
        for (TextReference entry : entries) {
            combo.add(entry.toShortString());
        }
        combo.select(0);
        combo.setSize(combo.computeSize(SWT.DEFAULT, SWT.DEFAULT));
        combo.getParent().layout();
        load(entries[0]);
    }

    private void load(TextReference entry) {
        if (textFile == null || !entry.getPath().equals(textFile.getPath())) {
            table.setItemCount(0);
            textFile = null;
            try {
                textFile = new TextFile(entry.getPath());
                int lineNumber = 1;
                for (String line : textFile.getLines()) {
                    TableItem item = new TableItem(table, SWT.NULL);
                    item.setText(0, "" + lineNumber);
                    item.setText(1, line);
                    lineNumber++;
                }
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }
        }
        table.setSelection(entry.getLine() - 1);
    }

}
