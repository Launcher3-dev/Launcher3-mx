package com.android.launcher3.setting;

import android.content.Context;

import com.android.launcher3.config.FeatureFlags;
import com.android.launcher3.effect.TransitionEffect;
import com.android.launcher3.util.LauncherSpUtil;

public final class MxSettings {


    // 特效标记
    public static int sLauncherEffect = TransitionEffect.TRANSITION_EFFECT_NONE;
//    public static int sLauncherEffect = 3;

    // 是否显示卸载按钮标记
    public static boolean sShowUnInstallIcon = true;

    /**
     * PagedView can scroll circle-endless.
     */
    private boolean isPagedViewCircleScroll = FeatureFlags.LAUNCHER3_CIRCLE_SCROLL;
    private Context mContext;


    private static class SettingHolder {
        private static final MxSettings MX_SETTINGS = new MxSettings();
    }

    public static MxSettings getInstance() {
        return SettingHolder.MX_SETTINGS;
    }

    public void loadSettings(Context context) {
        mContext = context.getApplicationContext();
//        sLauncherEffect = LauncherSpUtil.getIntData(mContext, LauncherSpUtil.KEY_SCROLL_EFFECT, TransitionEffect.TRANSITION_EFFECT_NONE);
        loadScreenCycle();
    }

    public void setPagedViewCircleScroll(boolean isPagedViewCircleScroll) {
        this.isPagedViewCircleScroll = isPagedViewCircleScroll;
        LauncherSpUtil.saveBooleanData(mContext, LauncherSpUtil.KEY_PAGE_CIRCLE, isPagedViewCircleScroll);
    }

    public void loadScreenCycle() {
        isPagedViewCircleScroll = LauncherSpUtil.getBooleanData(mContext, LauncherSpUtil.KEY_PAGE_CIRCLE);
    }

    public boolean isPageViewCircleScroll() {
        return isPagedViewCircleScroll;
    }

    public void setLauncherEffect(int effect) {
        sLauncherEffect = effect;
        LauncherSpUtil.saveIntData(mContext, LauncherSpUtil.KEY_SCROLL_EFFECT, effect);
    }

}
