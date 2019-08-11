package com.android.launcher3.menu;

import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;

import com.android.launcher3.InvariantDeviceProfile;
import com.android.launcher3.R;
import com.android.launcher3.Utilities;

/**
 * Created by CodeMX
 * DATE 2018/1/15
 * TIME 10:36
 */

public class MenuDeviceProfile {

    private Context mContext;

    public int menuCellWidthPx;
    public int menuCellHeightPx;
    public int menuIconSizePx;
    public int menuTextSizePx;
    public int menuCellCountX = 4;
    public int menuCellCountY = 1;

    public MenuDeviceProfile(Context context) {
        mContext = context;
        menuCellCountX = context.getResources().getInteger(R.integer.menu_num_columns);
    }

    public void updateIconSize(float scale, int drawablePadding, Resources res,
                               InvariantDeviceProfile inv, DisplayMetrics dm) {
        menuIconSizePx = (int) (Utilities.pxFromDp(inv.iconSize, dm) * scale);
        menuTextSizePx = (int) (Utilities.pxFromSp(inv.iconTextSize, dm) * scale);

        menuCellWidthPx = menuIconSizePx;
        menuCellHeightPx = menuIconSizePx + drawablePadding
                + Utilities.calculateTextHeight(menuTextSizePx);
    }

    public int getMenuCellCountX() {
        return menuCellCountX;
    }

    public int getMenuCellCountY() {
        return menuCellCountY;
    }

}
