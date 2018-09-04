package com.android.launcher3.setting;

import android.content.Context;
import android.content.SharedPreferences;

public final class Settings {

    private static final String SHARED = "launcher_share";
    private static final String SHARE_CYCLE = "share_cycle";

    /**
     * PagedView can scroll circle-endless.
     */
    public static boolean sIsPagedViewCircleScroll = true;

    private SharedPreferences mShared;


    private static class SettingHolder {
        private static final Settings SETTINGS = new Settings();
    }

    public static Settings getInstance() {
        return SettingHolder.SETTINGS;
    }

    public void loadSettings(Context context) {
        mShared = context.getSharedPreferences(SHARED, Context.MODE_PRIVATE);
    }

    public void setPagedViewCircleScroll(boolean isPagedViewCircleScroll) {
        Settings.sIsPagedViewCircleScroll = isPagedViewCircleScroll;
        mShared.edit().putInt(SHARE_CYCLE, isPagedViewCircleScroll ? 1 : 0).apply();
    }

    public void loadScreenCycle() {
        int isScreenCycle = mShared.getInt(SHARE_CYCLE, 0);
        sIsPagedViewCircleScroll = isScreenCycle == 1;
    }

}
