package com.codemx.floatwindow.permission.compat;

import android.annotation.TargetApi;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;

import com.android.mxlibrary.util.XLog;
import com.codemx.floatwindow.permission.RomUtil;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Created by yuchuan
 * DATE 2020/4/28
 * TIME 19:40
 */
public abstract class SettingCompat {

    private static final Object sInstanceLock = new Object();
    private static SettingCompat sInstance;

    public static SettingCompat getInstance() {
        synchronized (sInstanceLock) {
            if (sInstance == null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    sInstance = new AndroidMSettingCompat();
                } else {
                    if (RomUtil.isMiui()) {
                        sInstance = new MiUISettingCompat();
                    } else if (RomUtil.isEmui()) {
                        sInstance = new EMUISettingCompat();
                    } else if (RomUtil.isFlyme()) {
                        sInstance = new FlymeSettingCompat();
                    } else if (RomUtil.isOppo()) {
                        sInstance = new OppoSettingCompat();
                    } else if (RomUtil.isVivo()) {
                        sInstance = new VivoSettingCompat();
                    } else if (RomUtil.isQiku()) {
                        sInstance = new QikuSettingCompat();
                    } else if (RomUtil.isSmartisan()) {
                        sInstance = new SmartisanSettingCompat();
                    } else {
                        sInstance = new DefaultSettingCompat();
                    }
                }
            }
        }
        return sInstance;
    }

    public abstract boolean manageDrawOverlays(Context context);

    public abstract void startPermissionActivity(Context context);

    public abstract String getVersionCode(Context context);

    public boolean checkFloatWindowPermission(Context context) {
        if (Build.VERSION.SDK_INT >= 19) {
            return checkOp(context, 24); //OP_SYSTEM_ALERT_WINDOW = 24;
        }
        return true;
    }

    boolean startSafely(Context context, Intent intent) {
        if (context.getPackageManager().queryIntentActivities(intent,
                PackageManager.MATCH_DEFAULT_ONLY).size() > 0) {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
            return true;
        } else {
            Log.e(XLog.getTag(), "Intent is not available! " + intent);
            return false;
        }
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    boolean checkOp(Context context, int op) {
        AppOpsManager manager = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
        try {
            Class<?> clazz = AppOpsManager.class;
            Method method = clazz.getDeclaredMethod("checkOp", int.class, int.class, String.class);
            return AppOpsManager.MODE_ALLOWED == (int) method.invoke(manager, op, Binder.getCallingUid(), context.getPackageName());
        } catch (Exception e) {
            XLog.e(XLog.getTag(), Log.getStackTraceString(e));
        }
        return false;
    }

    // 原生系统启动方式
    protected void startPermissionDetailActivity(Context context) {
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

    private static final int OP_WRITE_SETTINGS = 23;
    private static final int OP_SYSTEM_ALERT_WINDOW = 24;

    public boolean canDrawOverlays(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return Settings.canDrawOverlays(context);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            return checkOp(context, OP_SYSTEM_ALERT_WINDOW);
        } else {
            return true;
        }
    }

    // 系统设置是否有写入权限
    public boolean canWriteSettings(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return Settings.System.canWrite(context);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            return checkOp(context, OP_WRITE_SETTINGS);
        } else {
            return true;
        }
    }

    // 设置允许显示Window
    public boolean setDrawOverlays(Context context, boolean allowed) {
        return setMode(context, OP_SYSTEM_ALERT_WINDOW, allowed);
    }

    public boolean setWriteSettings(Context context, boolean allowed) {
        return setMode(context, OP_WRITE_SETTINGS, allowed);
    }


    // 可设置Android 4.3/4.4的授权状态
    private boolean setMode(Context context, int op, boolean allowed) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR2
                || Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return false;
        }
        AppOpsManager manager = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
        try {
            Class<?> cls = AppOpsManager.class;
            Method method = cls.getDeclaredMethod("setMode", int.class, int.class,
                    String.class, int.class);
            method.invoke(manager, op, Binder.getCallingUid(), context.getPackageName(),
                    allowed ? AppOpsManager.MODE_ALLOWED : AppOpsManager
                            .MODE_IGNORED);
            return true;
        } catch (Exception e) {
            Log.e("AbstractSettingCompat", Log.getStackTraceString(e));
        }
        return false;
    }

}
