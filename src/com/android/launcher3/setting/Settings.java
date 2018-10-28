package com.android.launcher3.setting;

import android.content.Context;

import com.android.launcher3.effect.TransitionEffect;
import com.android.launcher3.util.LauncherSpUtil;

public final class Settings {


    // 特效标记
    public static int sLauncherEffect = TransitionEffect.TRANSITION_EFFECT_NONE;
//    public static int sLauncherEffect = 3;

    // 是否显示卸载按钮标记
    public static boolean sShowUnInstallIcon = false;

    /**
     * PagedView can scroll circle-endless.
     */
    public static boolean sIsPagedViewCircleScroll = true;
    private Context mContext;


    private static class SettingHolder {
        private static final Settings SETTINGS = new Settings();
    }

    public static Settings getInstance() {
        return SettingHolder.SETTINGS;
    }

    public void loadSettings(Context context) {
        mContext = context;
//        sLauncherEffect = LauncherSpUtil.getIntData(mContext, LauncherSpUtil.KEY_SCROLL_EFFECT, TransitionEffect.TRANSITION_EFFECT_NONE);
    }

    public void setPagedViewCircleScroll(boolean isPagedViewCircleScroll) {
        Settings.sIsPagedViewCircleScroll = isPagedViewCircleScroll;
        LauncherSpUtil.saveBooleanData(mContext, LauncherSpUtil.KEY_PAGE_CIRCLE, isPagedViewCircleScroll);
    }

    public void loadScreenCycle() {
        sIsPagedViewCircleScroll = LauncherSpUtil.getBooleanData(mContext, LauncherSpUtil.KEY_PAGE_CIRCLE);
    }

    public void setLauncherEffect(int effect) {
        sLauncherEffect = effect;
        LauncherSpUtil.saveIntData(mContext, LauncherSpUtil.KEY_SCROLL_EFFECT, effect);
    }

}
