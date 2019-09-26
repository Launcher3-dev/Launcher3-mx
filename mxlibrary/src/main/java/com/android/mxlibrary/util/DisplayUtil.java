package com.android.mxlibrary.util;

import android.content.Context;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;

public final class DisplayUtil {

    private static final String NAVBAR_STATE_CHANGED = "navbar_state_changed";
    private static final String GESTURE_REPLACE_NAVIGATION_BAR = "gesture_replace_navigation_bar";

    private static boolean isAboveO() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.O;
    }

    // 是否显示导航栏
    public static boolean isNavigationShow(Context context) {
        Log.e("TAG_GU", "isAboveO:  " + isAboveO() + "  " + Build.VERSION.SDK_INT);
        int navbarStateChangedId = Settings.System.getInt(context.getContentResolver(),
                isAboveO() ? GESTURE_REPLACE_NAVIGATION_BAR : NAVBAR_STATE_CHANGED, 0);
        Log.e("TAG_GU", (isAboveO() ? GESTURE_REPLACE_NAVIGATION_BAR : NAVBAR_STATE_CHANGED) + navbarStateChangedId);
        return navbarStateChangedId == 0;
    }

    // 是否显示手势
    public static boolean isGestureShow(Context context) {
        Log.e("TAG_GU", "isAboveO:  " + isAboveO() + "  " + Build.VERSION.SDK_INT);
        int navbarStateChangedId = Settings.System.getInt(context.getContentResolver(),
                isAboveO() ? GESTURE_REPLACE_NAVIGATION_BAR : NAVBAR_STATE_CHANGED, 0);
        Log.e("TAG_GU", (isAboveO() ? GESTURE_REPLACE_NAVIGATION_BAR : NAVBAR_STATE_CHANGED) + navbarStateChangedId);
        return navbarStateChangedId == 1;
    }

    /**
     * 获取手势导航或者底部导航的id
     *
     * @param context
     *
     * @return 1：手势导航；0：底部按钮导航
     */
    public static int getGestureReplaceNavigationBarId(Context context) {
        return Settings.System.getInt(context.getContentResolver(),
                isAboveO() ? GESTURE_REPLACE_NAVIGATION_BAR : NAVBAR_STATE_CHANGED, 0);
    }

}
