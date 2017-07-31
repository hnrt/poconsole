// Copyright (C) 2017 Hideaki Narita

package com.hideakin.app.view;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Button;

public class ButtonUtil {

    public static void adjustSize(Button button) {
        Point size = button.computeSize(SWT.DEFAULT, SWT.DEFAULT);
        if (size.x < size.y) {
            size.x = size.y;
        }
        button.setSize(size);
    }

}
