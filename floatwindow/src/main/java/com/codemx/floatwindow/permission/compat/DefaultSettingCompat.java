package com.codemx.floatwindow.permission.compat;

import android.content.Context;

/**
 * Created by yuchuan
 * DATE 2020/4/28
 * TIME 19:49
 */
public class DefaultSettingCompat extends SettingCompat {

    @Override
    public boolean manageDrawOverlays(Context context) {

        return false;
    }

    @Override
    public void startPermissionActivity(Context context) {

    }

    @Override
    public String getVersionCode(Context context) {
        return null;
    }
}
