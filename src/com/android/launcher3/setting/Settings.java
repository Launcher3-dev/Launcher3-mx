package com.android.launcher3.setting;

import android.content.Context;

import com.android.launcher3.util.LauncherSpUtil;

public final class Settings {

    /**
     * PagedView can scroll circle-endless.
     */
    public static boolean sIsPagedViewCircleScroll = false;
    private Context mContext;


    private static class SettingHolder {
        private static final Settings SETTINGS = new Settings();
    }

    public static Settings getInstance() {
        return SettingHolder.SETTINGS;
    }

    public void loadSettings(Context context) {
        mContext = context;
    }

    public void setPagedViewCircleScroll(boolean isPagedViewCircleScroll) {
        Settings.sIsPagedViewCircleScroll = isPagedViewCircleScroll;
        LauncherSpUtil.saveBooleanData(mContext, LauncherSpUtil.KEY_PAGE_CIRCLE, isPagedViewCircleScroll);
    }

    public void loadScreenCycle() {
        sIsPagedViewCircleScroll = LauncherSpUtil.getBooleanData(mContext, LauncherSpUtil.KEY_PAGE_CIRCLE);
    }

}
