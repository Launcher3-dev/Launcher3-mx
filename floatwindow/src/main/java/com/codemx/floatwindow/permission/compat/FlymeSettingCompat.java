package com.codemx.floatwindow.permission.compat;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.android.mxlibrary.util.XLog;


/**
 * Created by yuchuan
 * DATE 2020/4/28
 * TIME 19:46
 */
public class FlymeSettingCompat extends SettingCompat {

    @Override
    public boolean manageDrawOverlays(Context context) {
        Intent intent = new Intent("com.meizu.safe.security.SHOW_APPSEC");
        intent.setClassName("com.meizu.safe", "com.meizu.safe.security.AppSecActivity");
        intent.putExtra("packageName", context.getPackageName());
        return startSafely(context, intent);
    }

    @Override
    public void startPermissionActivity(Context context) {
        try {
            Intent intent = new Intent("com.meizu.safe.security.SHOW_APPSEC");
            intent.putExtra("packageName", context.getPackageName());
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } catch (Exception e) {
            try {
                XLog.e(XLog.getTag(), "获取悬浮窗权限, 打开AppSecActivity失败, " + Log.getStackTraceString(e));
                startPermissionDetailActivity(context);
            } catch (Exception eFinal) {
                XLog.e(XLog.getTag(), "获取悬浮窗权限失败, 通用获取方法失败, " + Log.getStackTraceString(eFinal));
            }
        }
    }

    @Override
    public String getVersionCode(Context context) {
        return "";
    }
}
