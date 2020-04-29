package com.codemx.floatwindow.permission.compat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

/**
 * Created by yuchuan
 * DATE 2020/4/28
 * TIME 19:48
 */
public class SmartisanSettingCompat extends SettingCompat {

    @Override
    public boolean manageDrawOverlays(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return false;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // 锤子 坚果|5.1.1|2.5.3
            Intent intent = new Intent("com.smartisanos.security.action.SWITCHED_PERMISSIONS_NEW");
            intent.setClassName("com.smartisanos.security",
                    "com.smartisanos.security.SwitchedPermissions");
            intent.putExtra("index", 17); // 不同版本会不一样
            return startSafely(context, intent);
        } else {
            // 锤子 坚果|4.4.4|2.1.2
            Intent intent = new Intent("com.smartisanos.security.action.SWITCHED_PERMISSIONS");
            intent.setClassName("com.smartisanos.security",
                    "com.smartisanos.security.SwitchedPermissions");
            intent.putExtra("permission", new String[]{Manifest.permission.SYSTEM_ALERT_WINDOW});

            //        Intent intent = new Intent("com.smartisanos.security.action.MAIN");
            //        intent.setClassName("com.smartisanos.security", "com.smartisanos.security
            //        .MainActivity");
            return startSafely(context, intent);
        }
    }

    @Override
    public void startPermissionActivity(Context context) {

    }

    @Override
    public String getVersionCode(Context context) {
        return null;
    }
}
