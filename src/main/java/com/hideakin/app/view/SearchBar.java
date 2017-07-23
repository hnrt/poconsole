package com.hideakin.app.view;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.hideakin.app.controller.EventHandler;
import com.hideakin.app.view.layout.CustomLayout;
import com.hideakin.app.view.layout.CustomLayoutData;

public class SearchBar extends Composite {

    private static final String HEADER_LABEL = "Find:";
    private static final String NOT_FOUND = "[Not found]";

    private Text text;
    private Label resultLabel;
    private boolean clearResult;

    public SearchBar(Composite parent) {
        super(parent, SWT.NULL);

        CustomLayout layout = new CustomLayout(SWT.HORIZONTAL);
        this.setLayout(layout);

        Label headerLabel = new Label(this, SWT.NULL);
        headerLabel.setText(HEADER_LABEL);
        headerLabel.setLayoutData(new CustomLayoutData(CustomLayoutData.LEFT | CustomLayoutData.VCENTER));

        text = new Text(this, SWT.SINGLE | SWT.BORDER);
        text.setLayoutData(new CustomLayoutData(CustomLayoutData.LEFT | CustomLayoutData.VCENTER));
        text.setSize(200, text.computeSize(SWT.DEFAULT, SWT.DEFAULT).y);

        Button dnButton = new Button(this, SWT.PUSH);
        dnButton.setText("\u25bc");
        dnButton.setToolTipText("Forward");
        dnButton.setLayoutData(new CustomLayoutData(CustomLayoutData.LEFT | CustomLayoutData.VCENTER));
        adjustButtonSize(dnButton);

        Button upButton = new Button(this, SWT.PUSH);
        upButton.setText("\u25b2");
        upButton.setToolTipText("Backward");
        upButton.setLayoutData(new CustomLayoutData(CustomLayoutData.LEFT | CustomLayoutData.VCENTER));
        adjustButtonSize(upButton);

        Button caseButton = new Button(this, SWT.CHECK);
        caseButton.setText("Case sensitive");
        caseButton.setLayoutData(new CustomLayoutData(CustomLayoutData.LEFT | CustomLayoutData.VCENTER));
        caseButton.setSelection(false);

        Button keyButton = new Button(this, SWT.CHECK);
        keyButton.setText("msgid");
        keyButton.setLayoutData(new CustomLayoutData(CustomLayoutData.LEFT | CustomLayoutData.VCENTER));
        keyButton.setSelection(true);

        Button valButton = new Button(this, SWT.CHECK);
        valButton.setText("msgstr");
        valButton.setLayoutData(new CustomLayoutData(CustomLayoutData.LEFT | CustomLayoutData.VCENTER));
        valButton.setSelection(true);

        resultLabel = new Label(this, SWT.NULL);
        resultLabel.setLayoutData(new CustomLayoutData(CustomLayoutData.LEFT | CustomLayoutData.VCENTER, 30, 0));
        resultLabel.setSize(200, resultLabel.computeSize(SWT.DEFAULT, SWT.DEFAULT).y);
        resultLabel.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_RED));
        clearResult = false;
        
        Button closeButton = new Button(this, SWT.PUSH);
        closeButton.setText("\u2613");
        closeButton.setToolTipText("Hide");
        closeButton.setLayoutData(new CustomLayoutData(CustomLayoutData.RIGHT | CustomLayoutData.VCENTER));
        adjustButtonSize(closeButton);

        closeButton.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetDefaultSelected(SelectionEvent event) {
                widgetSelected(event);
            }
            @Override
            public void widgetSelected(SelectionEvent event) {
                SearchBar.this.setVisible(false);
                parent.layout();
            }
        });

        text.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(ModifyEvent event) {
                if (clearResult) {
                    showResult(null);
                }
            }
        });

        dnButton.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetDefaultSelected(SelectionEvent event) {
                widgetSelected(event);
            }
            @Override
            public void widgetSelected(SelectionEvent event) {
                int subject = 0;
                if (keyButton.getSelection()) {
                    subject |= EventHandler.KEY; 
                }
                if (valButton.getSelection()) {
                    subject |= EventHandler.VAL; 
                }
                int rc = EventHandler.getInstance().find(text.getText(), caseButton.getSelection(), subject, true);
                if (rc == 0) {
                    showResult(NOT_FOUND);
                }
            }
        });

        upButton.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetDefaultSelected(SelectionEvent event) {
                widgetSelected(event);
            }
            @Override
            public void widgetSelected(SelectionEvent event) {
                int subject = 0;
                if (keyButton.getSelection()) {
                    subject |= EventHandler.KEY; 
                }
                if (valButton.getSelection()) {
                    subject |= EventHandler.VAL; 
                }
                int rc = EventHandler.getInstance().find(text.getText(), caseButton.getSelection(), subject, false);
                if (rc == 0) {
                    showResult(NOT_FOUND);
                }
            }
        });
    }

    private void adjustButtonSize(Button button) {
        Point size = button.computeSize(SWT.DEFAULT, SWT.DEFAULT);
        if (size.x < size.y) {
            size.x = size.y;
        }
        button.setSize(size);
    }

    private void showResult(String result) {
        if (result == null) {
            result = "";
        }
        resultLabel.setText(result);
        clearResult = !result.isEmpty();
    }

}
