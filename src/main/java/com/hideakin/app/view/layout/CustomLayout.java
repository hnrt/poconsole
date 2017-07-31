// Copyright (C) 2017 Hideaki Narita

package com.hideakin.app.view.layout;

import java.util.Arrays;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Layout;

public class CustomLayout extends Layout {

    private static final boolean DEBUG = false;

    private int direction;

    public CustomLayout(int direction) {
        switch (direction)
        {
        case SWT.HORIZONTAL:
        case SWT.VERTICAL:
            this.direction = direction;
            break;
        default:
            throw new RuntimeException("CustomLayout: Bad direction: " + direction);
        }
    }

    @Override
    protected Point computeSize(Composite composite, int wHint, int hHint, boolean flushCache) {
        switch (direction)
        {
        case SWT.HORIZONTAL:
            return computeSizeHorizontally(composite);
        case SWT.VERTICAL:
            return computeSizeVertically(composite);
        }
        return new Point(0, 0);
    }

    @Override
    protected void layout(Composite parent, boolean arg1) {
        switch (direction)
        {
        case SWT.HORIZONTAL:
            layoutHorizontally(parent);
            break;
        case SWT.VERTICAL:
            layoutVertically(parent);
            break;
        }
    }

    private Point computeSizeHorizontally(Composite composite) {
        Rectangle ca = composite.getClientArea();
        if (DEBUG) {
            System.out.println("computeSizeHorizontally: ca=" + ca);
        }
        Control[] children = composite.getChildren();
        Point[] sizes = new Point[children.length];
        Arrays.fill(sizes, null);
        int cx = 0;
        int cy = 0;
        int cf = 0;
        int fx = 0;
        for (int index = 0; index < children.length; index++) {
            Control child = children[index];
            if (child.getVisible()) {
                Object obj = child.getLayoutData();
                if (obj.getClass() == CustomLayoutData.class) {
                    CustomLayoutData data = (CustomLayoutData) obj;
                    sizes[index] = child.getSize();
                    if (sizes[index].x <= 0 || sizes[index].y <= 0) {
                        Point size = child.computeSize(SWT.DEFAULT, SWT.DEFAULT);
                        if (sizes[index].x <= 0) {
                            sizes[index].x = size.x;
                        }
                        if (sizes[index].y <= 0) {
                            sizes[index].y = size.y;
                        }
                    }
                    switch (data.flags & (CustomLayoutData.HCENTER | CustomLayoutData.HFILL)) {
                    case CustomLayoutData.HCENTER:
                        fx += sizes[index].x;
                        //FALLTHROUGH
                    case CustomLayoutData.HFILL:
                        cf++;
                        break;
                    default:
                        cx += sizes[index].x + data.hOffset;
                        break;
                    }
                    switch (data.flags & (CustomLayoutData.VCENTER | CustomLayoutData.VFILL)) {
                    case CustomLayoutData.VCENTER:
                        if (cy < sizes[index].y) {
                            cy = sizes[index].y;
                        }
                        break;
                    case CustomLayoutData.VFILL:
                        if (ca.height > 0) {
                            cy = ca.height;
                        } else {
                            cy = sizes[index].y;
                        }
                        break;
                    default:
                        if (cy < sizes[index].y + data.vOffset) {
                            cy = sizes[index].y + data.vOffset;
                        }
                        break;
                    }
                }
            }
        }
        if (cf > 0) {
            if (cx + fx < ca.width) {
                cx = ca.width;
            } else {
                cx += fx;
            }
        }
        if (DEBUG) {
            System.out.println("computeSizeHorizontally: " + new Point(cx, cy));
        }
        return new Point(cx, cy);
    }

    private Point computeSizeVertically(Composite composite) {
        Rectangle ca = composite.getClientArea();
        if (DEBUG) {
            System.out.println("computeSizeVertically: ca=" + ca);
        }
        Control[] children = composite.getChildren();
        Point[] sizes = new Point[children.length];
        Arrays.fill(sizes, null);
        int cx = 0;
        int cy = 0;
        int cf = 0;
        int fy = 0;
        for (int index = 0; index < children.length; index++) {
            Control child = children[index];
            if (child.getVisible()) {
                Object obj = child.getLayoutData();
                if (obj.getClass() == CustomLayoutData.class) {
                    CustomLayoutData data = (CustomLayoutData) obj;
                    sizes[index] = child.getSize();
                    if (sizes[index].x <= 0 || sizes[index].y <= 0) {
                        Point size = child.computeSize(SWT.DEFAULT, SWT.DEFAULT);
                        if (sizes[index].x <= 0) {
                            sizes[index].x = size.x;
                        }
                        if (sizes[index].y <= 0) {
                            sizes[index].y = size.y;
                        }
                    }
                    switch (data.flags & (CustomLayoutData.HCENTER | CustomLayoutData.HFILL)) {
                    case CustomLayoutData.HCENTER:
                        if (cx < sizes[index].x) {
                            cx = sizes[index].x;
                        }
                        break;
                    case CustomLayoutData.HFILL:
                        if (ca.width > 0) {
                            cx = ca.width;
                        } else {
                            cx = sizes[index].x;
                        }
                        break;
                    default:
                        if (cx < sizes[index].x + data.hOffset) {
                            cx = sizes[index].x + data.hOffset;
                        }
                        break;
                    }
                    switch (data.flags & (CustomLayoutData.VCENTER | CustomLayoutData.VFILL)) {
                    case CustomLayoutData.VCENTER:
                        fy += sizes[index].y;
                        //FALLTHROUGH
                    case CustomLayoutData.VFILL:
                        cf++;
                        break;
                    default:
                        cy += sizes[index].y + data.vOffset;
                        break;
                    }
                }
            }
        }
        if (cf > 0) {
            if (cy + fy < ca.width) {
                cy = ca.width;
            } else {
                cy += fy;
            }
        }
        if (DEBUG) {
            System.out.println("computeSizeVertically: " + new Point(cx, cy));
        }
        return new Point(cx, cy);
    }

    private void layoutHorizontally(Composite composite) {
        Rectangle ca = composite.getClientArea();
        if (DEBUG) {
            System.out.println("layoutHorizontally: ca=" + ca);
        }
        Control[] children = composite.getChildren();
        Point[] sizes = new Point[children.length];
        Arrays.fill(sizes, null);
        int cx0 = 0;
        int cy0 = 0;
        int cf = 0;
        int fx = 0;
        for (int index = 0; index < children.length; index++) {
            Control child = children[index];
            if (child.getVisible()) {
                Object obj = child.getLayoutData();
                if (obj.getClass() == CustomLayoutData.class) {
                    CustomLayoutData data = (CustomLayoutData) obj;
                    sizes[index] = child.getSize();
                    if (sizes[index].x <= 0 || sizes[index].y <= 0) {
                        Point size = child.computeSize(SWT.DEFAULT, SWT.DEFAULT);
                        if (sizes[index].x <= 0) {
                            sizes[index].x = size.x;
                        }
                        if (sizes[index].y <= 0) {
                            sizes[index].y = size.y;
                        }
                    }
                    switch (data.flags & (CustomLayoutData.HCENTER | CustomLayoutData.HFILL)) {
                    case CustomLayoutData.HCENTER:
                        fx += sizes[index].x;
                        //FALLTHOUGH
                    case CustomLayoutData.HFILL:
                        cf++;
                        break;
                    default:
                        cx0 += sizes[index].x + data.hOffset;
                        break;
                    }
                    switch (data.flags & (CustomLayoutData.VCENTER | CustomLayoutData.VFILL)) {
                    case CustomLayoutData.VCENTER:
                        if (cy0 < sizes[index].y) {
                            cy0 = sizes[index].y;
                        }
                        break;
                    case CustomLayoutData.VFILL:
                        cy0 = ca.height;
                        break;
                    default:
                        if (cy0 < sizes[index].y + data.vOffset) {
                            cy0 = sizes[index].y + data.vOffset;
                        }
                        break;
                    }
                }
            }
        }
        if (cy0 > ca.height) {
            cy0 = ca.height;
        }
        int x1 = 0;
        int x2 = Math.max(ca.width, cx0 + fx);
        for (int index = 0; index < children.length; index++) {
            Control child = children[index];
            if (child.getVisible()) {
                Object obj = child.getLayoutData();
                if (obj.getClass() == CustomLayoutData.class) {
                    CustomLayoutData data = (CustomLayoutData) obj;
                    int x;
                    int y;
                    int cx;
                    int cy;
                    int xx;
                    switch (data.flags & CustomLayoutData.HMASK) {
                    case CustomLayoutData.LEFT:
                        cx = sizes[index].x;
                        x = x1 + data.hOffset;
                        x1 = x + cx;
                        break;
                    case CustomLayoutData.RIGHT:
                        cx = sizes[index].x;
                        x2 -= cx + data.hOffset;
                        x = x2;
                        break;
                    case (CustomLayoutData.LEFT | CustomLayoutData.HCENTER):
                        cx = sizes[index].x;
                        xx = Math.max(cx, (ca.width - cx0) / cf);
                        x = x1 + (xx - cx) / 2;
                        x1 += xx;
                        cf--;
                        cx0 += xx;
                        break;
                    case (CustomLayoutData.RIGHT | CustomLayoutData.HCENTER):
                        cx = sizes[index].x;
                        xx = Math.max(cx, (ca.width - cx0) / cf);
                        x2 -= xx;
                        x = x2 + (xx - cx) / 2;
                        cf--;
                        cx0 += xx;
                        break;
                    case (CustomLayoutData.LEFT | CustomLayoutData.HFILL):
                        cx = Math.max(0, (ca.width - cx0) / cf);
                        x = x1;
                        x1 += cx;
                        cf--;
                        cx0 += cx;
                        break;
                    case (CustomLayoutData.RIGHT | CustomLayoutData.HFILL):
                        cx = Math.max(0, (ca.width - cx0) / cf);
                        x2 -= cx;
                        x = x2;
                        cf--;
                        cx0 += cx;
                        break;
                    default:
                        throw new RuntimeException("layoutHorizontally: Bad flags: " + data.flags);
                    }
                    switch (data.flags & CustomLayoutData.VMASK) {
                    case CustomLayoutData.TOP:
                        y = data.vOffset;
                        cy = sizes[index].y;
                        break;
                    case CustomLayoutData.VFILL:
                        y = 0;
                        cy = cy0;
                        break;
                    case CustomLayoutData.BOTTOM:
                        y = cy0 - (sizes[index].y + data.vOffset);
                        cy = sizes[index].y;
                        break;
                    case CustomLayoutData.VCENTER:
                        y = (cy0 - sizes[index].y) / 2;
                        cy = sizes[index].y;
                        break;
                    default:
                        throw new RuntimeException("layoutHorizontally: Bad flags: " + data.flags);
                    }
                    child.setBounds(new Rectangle(x, y, cx, cy));
                    if (DEBUG) {
                        System.out.println("layoutHorizontally: " + child.getClass().getName() + ": " + child.getBounds());
                    }
                }
            }
        }
    }

    private void layoutVertically(Composite composite) {
        Rectangle ca = composite.getClientArea();
        if (DEBUG) {
            System.out.println("layoutVertically: ca=" + ca);
        }
        Control[] children = composite.getChildren();
        Point[] sizes = new Point[children.length];
        Arrays.fill(sizes, null);
        int cx0 = 0;
        int cy0 = 0;
        int cf = 0;
        int fy = 0;
        for (int index = 0; index < children.length; index++) {
            Control child = children[index];
            if (child.getVisible()) {
                Object obj = child.getLayoutData();
                if (obj.getClass() == CustomLayoutData.class) {
                    CustomLayoutData data = (CustomLayoutData) obj;
                    sizes[index] = child.getSize();
                    if (sizes[index].x <= 0 || sizes[index].y <= 0) {
                        Point size = child.computeSize(SWT.DEFAULT, SWT.DEFAULT);
                        if (sizes[index].x <= 0) {
                            sizes[index].x = size.x;
                        }
                        if (sizes[index].y <= 0) {
                            sizes[index].y = size.y;
                        }
                    }
                    switch (data.flags & (CustomLayoutData.HCENTER | CustomLayoutData.HFILL)) {
                    case CustomLayoutData.HCENTER:
                        if (cx0 < sizes[index].x) {
                            cx0 = sizes[index].x;
                        }
                        break;
                    case CustomLayoutData.HFILL:
                        cx0 = ca.width;
                        break;
                    default:
                        if (cx0 < sizes[index].x + data.hOffset) {
                            cx0 = sizes[index].x + data.hOffset;
                        }
                        break;
                    }
                    switch (data.flags & (CustomLayoutData.VCENTER | CustomLayoutData.VFILL)) {
                    case CustomLayoutData.VCENTER:
                        fy += sizes[index].y;
                        //FALLTHROUGH
                    case CustomLayoutData.VFILL:
                        cf++;
                        break;
                    default:
                        cy0 += sizes[index].y + data.vOffset;
                        break;
                    }
                }
            }
        }
        if (cx0 > ca.width) {
            cx0 = ca.width;
        }
        int y1 = 0;
        int y2 = Math.max(ca.height, cy0 + fy);
        for (int index = 0; index < children.length; index++) {
            Control child = children[index];
            if (child.getVisible()) {
                Object obj = child.getLayoutData();
                if (obj.getClass() == CustomLayoutData.class) {
                    CustomLayoutData data = (CustomLayoutData) obj;
                    int x;
                    int y;
                    int cx;
                    int cy;
                    int yy;
                    switch (data.flags & CustomLayoutData.HMASK) {
                    case CustomLayoutData.LEFT:
                        x = data.hOffset;
                        cx = sizes[index].x;
                        break;
                    case CustomLayoutData.HFILL:
                        x = 0;
                        cx = cx0;
                        break;
                    case CustomLayoutData.RIGHT:
                        x = cx0 - (sizes[index].x + data.hOffset);
                        cx = sizes[index].x;
                        break;
                    case CustomLayoutData.HCENTER:
                        x = (cx0 - sizes[index].x) / 2;
                        cx = sizes[index].x;
                        break;
                    default:
                        throw new RuntimeException("layoutVertically: Bad flags: " + data.flags);
                    }
                    switch (data.flags & CustomLayoutData.VMASK) {
                    case CustomLayoutData.TOP:
                        cy = sizes[index].y;
                        y = y1 + data.vOffset;
                        y1 = y + cy;
                        break;
                    case CustomLayoutData.BOTTOM:
                        cy = sizes[index].y;
                        y2 -= cy + data.vOffset;
                        y = y2;
                        break;
                    case (CustomLayoutData.TOP | CustomLayoutData.VCENTER):
                        cy = sizes[index].y;
                        yy = Math.max(cy, (ca.height - cy0) / cf);
                        y = y1 + (yy - cy) / 2;
                        y1 += yy;
                        cf--;
                        cy0 += yy;
                        break;
                    case (CustomLayoutData.BOTTOM | CustomLayoutData.VCENTER):
                        cy = sizes[index].y;
                        yy = Math.max(cy, (ca.height - cy0) / cf);
                        y2 -= yy;
                        y = y2 + (yy - cy) / 2;
                        cf--;
                        cy0 += yy;
                        break;
                    case (CustomLayoutData.TOP | CustomLayoutData.VFILL):
                        cy = Math.max(0, (ca.height - cy0) / cf);
                        y = y1;
                        y1 += cy;
                        cf--;
                        cy0 += cy;
                        break;
                    case (CustomLayoutData.BOTTOM | CustomLayoutData.VFILL):
                        cy = Math.max(0, (ca.height - cy0) / cf);
                        y2 -= cy;
                        y = y2;
                        cf--;
                        cy0 += cy;
                        break;
                    default:
                        throw new RuntimeException("layoutVertically: Bad flags: " + data.flags);
                    }
                    child.setBounds(new Rectangle(x, y, cx, cy));
                    if (DEBUG) {
                        System.out.println("layoutVertically: " + child.getClass().getName() + ": " + child.getBounds());
                    }
                }
            }
        }
    }

}
