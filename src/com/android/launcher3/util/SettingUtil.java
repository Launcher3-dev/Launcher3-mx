package com.android.mxlibrary.util;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;

import java.util.ArrayList;
import java.util.List;

public final class SettingUtil {

    /**
     * 清理默认桌面设置
     *
     * @param context           上下文
     * @param launcherClassName Launcher 类名（Launcher.class.getName()）
     */
    public static void clearDefaultLauncher(Context context, String launcherClassName) {
        PackageManager pm = context.getPackageManager();
        String pn = context.getPackageName();
        ComponentName cn = new ComponentName(pn, launcherClassName);
        Intent homeIntent = new Intent("android.intent.action.MAIN");
        homeIntent.addCategory("android.intent.category.HOME");
        homeIntent.addCategory("android.intent.category.DEFAULT");
        homeIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        pm.setComponentEnabledSetting(cn, 1, 1);
        context.startActivity(homeIntent);
        pm.setComponentEnabledSetting(cn, 0, 1);
    }

    /**
     * 设置默认桌面
     *
     * @param context 上下文
     */
    public static void setLauncherDefault(Context context) {
        String pkgName = "android";
        String clsName = "com.android.internal.app.ResolverActivity";
        if ("huawei".equals(DeviceInfoUtil.getPhoneModel())) {
            pkgName = "com.huawei.android.internal.app";
            clsName = "com.huawei.android.internal.app.HwResolverActivity";
        }
        Intent paramIntent = new Intent("android.intent.action.MAIN");
        paramIntent.setComponent(new ComponentName(pkgName, clsName));
        paramIntent.addCategory("android.intent.category.DEFAULT");
        paramIntent.addCategory("android.intent.category.HOME");
        context.startActivity(paramIntent);
    }

    public static void resetPreferredLauncherAndOpenChooser(Context context) {
        PackageManager packageManager = context.getPackageManager();
        ComponentName componentName = new ComponentName(context, Launcher.class);
        packageManager.setComponentEnabledSetting(componentName, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);

        Intent selector = new Intent(Intent.ACTION_MAIN);
        selector.addCategory(Intent.CATEGORY_HOME);
        selector.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(selector);

        packageManager.setComponentEnabledSetting(componentName, PackageManager.COMPONENT_ENABLED_STATE_DEFAULT, PackageManager.DONT_KILL_APP);
    }

    /**
     * method checks to see if app is currently set as default launcher
     *
     * @return boolean true means currently set as default, otherwise false
     */
    public static boolean isMyAppLauncherDefault(Context context) {
        final IntentFilter filter = new IntentFilter(Intent.ACTION_MAIN);
        filter.addCategory(Intent.CATEGORY_HOME);

        List<IntentFilter> filters = new ArrayList<IntentFilter>();
        filters.add(filter);

        final String myPackageName = context.getPackageName();
        List<ComponentName> activities = new ArrayList<ComponentName>();
        final PackageManager packageManager = context.getPackageManager();
        packageManager.getPreferredActivities(filters, activities, null);
        for (ComponentName activity : activities) {
            if (myPackageName.equals(activity.getPackageName())) {
                return true;
            }
        }
        return false;
    }

}
