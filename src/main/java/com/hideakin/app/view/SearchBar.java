package com.hideakin.app.view;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

import com.hideakin.app.controller.EventHandler;
import com.hideakin.app.view.layout.CustomLayout;
import com.hideakin.app.view.layout.CustomLayoutData;

public class SearchBar extends Composite {

    private Text text;

    public SearchBar(Composite parent) {
        super(parent, SWT.NULL);

        CustomLayout layout = new CustomLayout(SWT.HORIZONTAL);
        this.setLayout(layout);

        text = new Text(this, SWT.SINGLE | SWT.BORDER);
        text.setLayoutData(new CustomLayoutData(CustomLayoutData.LEFT | CustomLayoutData.VCENTER));
        text.setSize(200, text.computeSize(SWT.DEFAULT, SWT.DEFAULT).y);

        Button dnButton = new Button(this, SWT.PUSH);
        dnButton.setText("\u25bc");
        dnButton.setLayoutData(new CustomLayoutData(CustomLayoutData.LEFT | CustomLayoutData.VCENTER));
        adjustButtonSize(dnButton);

        Button upButton = new Button(this, SWT.PUSH);
        upButton.setText("\u25b2");
        upButton.setLayoutData(new CustomLayoutData(CustomLayoutData.LEFT | CustomLayoutData.VCENTER));
        adjustButtonSize(upButton);

        Button caseButton = new Button(this, SWT.CHECK);
        caseButton.setText("Case sensitive");
        caseButton.setLayoutData(new CustomLayoutData(CustomLayoutData.LEFT | CustomLayoutData.VCENTER));

        Button closeButton = new Button(this, SWT.PUSH);
        closeButton.setText("\u2613");
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

        dnButton.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetDefaultSelected(SelectionEvent event) {
                widgetSelected(event);
            }
            @Override
            public void widgetSelected(SelectionEvent event) {
                EventHandler.getInstance().findForward(text.getText(), caseButton.getSelection());
            }
        });

        upButton.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetDefaultSelected(SelectionEvent event) {
                widgetSelected(event);
            }
            @Override
            public void widgetSelected(SelectionEvent event) {
                EventHandler.getInstance().findBackward(text.getText(), caseButton.getSelection());
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
}
