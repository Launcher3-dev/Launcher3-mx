package com.codemx.floatwindow.permission.compat;

import android.content.Context;
import android.content.Intent;

/**
 * Created by yuchuan
 * DATE 2020/4/28
 * TIME 19:47
 */
public class VivoSettingCompat extends SettingCompat {
    @Override
    public boolean manageDrawOverlays(Context context) {
        // 不支持直接到达悬浮窗设置页，只能到 i管家 首页
        Intent intent = new Intent("com.iqoo.secure");
        intent.setClassName("com.iqoo.secure", "com.iqoo.secure.MainActivity");
        // com.iqoo.secure.ui.phoneoptimize.SoftwareManagerActivity
        // com.iqoo.secure.ui.phoneoptimize.FloatWindowManager
        return startSafely(context, intent);
    }

    @Override
    public void startPermissionActivity(Context context) {

    }

    @Override
    public String getVersionCode(Context context) {
        return null;
    }
}
