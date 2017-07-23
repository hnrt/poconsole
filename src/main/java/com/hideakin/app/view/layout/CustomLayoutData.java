package com.hideakin.app.view.layout;

public class CustomLayoutData {

    public static final int LEFT = (1 << 0);
    public static final int RIGHT = (1 << 1);
    public static final int HCENTER = (1 << 2);
    public static final int HFILL = (1 << 3);
    public static final int LCENTER = LEFT | HCENTER;
    public static final int RCENTER = RIGHT | HCENTER;
    public static final int HMASK = LEFT | RIGHT | HCENTER | HFILL;
    public static final int TOP = (1 << 4);
    public static final int BOTTOM = (1 << 5);
    public static final int VCENTER = (1 << 6);
    public static final int VFILL = (1 << 7);
    public static final int TCENTER = TOP | VCENTER;
    public static final int BCENTER = BOTTOM | VCENTER;
    public static final int VMASK = TOP | BOTTOM | VCENTER | VFILL;
    public static final int FILL = HFILL | VFILL;

    public int flags;
    public int hOffset;
    public int vOffset;

    public CustomLayoutData(int flags, int hOffset, int vOffset) {
        this.flags = flags;
        this.hOffset = hOffset;
        this.vOffset = vOffset;
        switch (flags & HMASK) {
        case LEFT:
        case RIGHT:
        case HCENTER:
        case HFILL:
        case (LEFT | HCENTER):
        case (RIGHT | HCENTER):
        case (LEFT | HFILL):
        case (RIGHT | HFILL):
            break;
        default:
            throw new RuntimeException("CustomLayoutData: Bad flags: " + flags);
        }
        switch (flags & VMASK) {
        case TOP:
        case BOTTOM:
        case VCENTER:
        case VFILL:
        case (TOP | VCENTER):
        case (BOTTOM | VCENTER):
        case (TOP | VFILL):
        case (BOTTOM | VFILL):
            break;
        default:
            throw new RuntimeException("CustomLayoutData: Bad flags: " + flags);
        }
    }

    public CustomLayoutData(int flags) {
        this(flags, 0, 0);
    }

}
