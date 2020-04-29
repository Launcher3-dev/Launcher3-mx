package com.codemx.floatwindow.permission.compat;

import android.content.Context;

import com.codemx.floatwindow.permission.RomUtil;

/**
 * Created by yuchuan
 * DATE 2020/4/29
 * TIME 15:33
 */
public class GioneeSettingCompat extends SettingCompat {
    @Override
    public boolean manageDrawOverlays(Context context) {
        return false;
    }

    @Override
    public void startPermissionActivity(Context context) {

    }

    @Override
    public String getVersionCode(Context context) {
        try {
            String gioneeVersion = RomUtil.getPropS("ro.gn.gnznvernuber");
//            String gioneeVersion = RomUtil.getPropS("ro.build.version.release");
            return gioneeVersion.substring(gioneeVersion.indexOf("_") + 1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
}
