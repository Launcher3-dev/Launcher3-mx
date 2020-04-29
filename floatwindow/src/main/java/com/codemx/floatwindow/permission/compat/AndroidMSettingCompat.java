package com.codemx.floatwindow.permission.compat;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;

import com.codemx.floatwindow.permission.RomUtil;

import java.lang.reflect.Field;

/**
 * Created by yuchuan
 * DATE 2020/4/28
 * TIME 21:52
 */
@TargetApi(Build.VERSION_CODES.M)
public class AndroidMSettingCompat extends SettingCompat {

    @Override
    public boolean manageDrawOverlays(Context context) {
        if (RomUtil.isVivo()) {
            startPermissionDetailActivity(context);
        } else {
            //启动Activity让用户授权
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
            intent.setData(Uri.parse("package:" + context.getPackageName()));
            context.startActivity(intent);
        }
        return false;
    }

    @Override
    public void startPermissionActivity(Context context) {
        try {
            Class clazz = Settings.class;
            Field field = clazz.getDeclaredField("ACTION_MANAGE_OVERLAY_PERMISSION");
            Intent intent = new Intent(field.get(null).toString());
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setData(Uri.parse("package:" + context.getPackageName()));
            context.startActivity(intent);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getVersionCode(Context context) {
        return null;
    }
}
