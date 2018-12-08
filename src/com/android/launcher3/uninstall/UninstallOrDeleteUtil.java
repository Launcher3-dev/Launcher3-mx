package com.android.launcher3.uninstall;

import android.appwidget.AppWidgetHostView;
import android.appwidget.AppWidgetProviderInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.LauncherActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.UserHandle;
import android.os.UserManager;
import android.util.ArrayMap;
import android.view.View;
import android.widget.Toast;

import com.android.launcher3.ItemInfo;
import com.android.launcher3.ItemInfoWithIcon;
import com.android.launcher3.Launcher;
import com.android.launcher3.LauncherAppWidgetInfo;
import com.android.launcher3.LauncherAppWidgetProviderInfo;
import com.android.launcher3.LauncherSettings;
import com.android.launcher3.R;
import com.android.launcher3.compat.LauncherAppsCompat;
import com.android.mxlibrary.util.XLog;

import java.net.URISyntaxException;

import static android.appwidget.AppWidgetManager.INVALID_APPWIDGET_ID;
import static android.appwidget.AppWidgetProviderInfo.WIDGET_FEATURE_RECONFIGURABLE;
import static com.android.launcher3.ItemInfoWithIcon.FLAG_SYSTEM_MASK;
import static com.android.launcher3.ItemInfoWithIcon.FLAG_SYSTEM_NO;
import static com.android.launcher3.LauncherSettings.Favorites.CONTAINER_DESKTOP;

/**
 * 卸载或者删除应用图标
 */
public final class UninstallOrDeleteUtil {

    private static final ArrayMap<UserHandle, Boolean> mUninstallDisabledCache = new ArrayMap<>(1);

    /**
     * 是否支持卸载
     *
     * @param launcher Launcher
     * @param info     要卸载应用对象
     *
     * @return
     */
    public static boolean supportsUninstall(Launcher launcher, ItemInfo info) {
        return supportsAccessibilityDrop(launcher, info, getViewUnderDrag(launcher, info));
    }

    private static View getViewUnderDrag(Launcher launcher, ItemInfo info) {
        if (info instanceof LauncherAppWidgetInfo && info.container == CONTAINER_DESKTOP &&
                launcher.getWorkspace().getDragInfo() != null) {
            return launcher.getWorkspace().getDragInfo().cell;
        }
        return null;
    }

    private static boolean supportsAccessibilityDrop(Launcher launcher, ItemInfo info, View view) {
        if (view instanceof AppWidgetHostView) {// 插件
            if (getReconfigurableWidgetId(view) != INVALID_APPWIDGET_ID) {
                return true;
            }
            return false;
        }

        Boolean uninstallDisabled = mUninstallDisabledCache.get(info.user);
        if (uninstallDisabled == null) {
            UserManager userManager =
                    (UserManager) launcher.getSystemService(Context.USER_SERVICE);
            Bundle restrictions = userManager.getUserRestrictions(info.user);
            uninstallDisabled = restrictions.getBoolean(UserManager.DISALLOW_APPS_CONTROL, false)
                    || restrictions.getBoolean(UserManager.DISALLOW_UNINSTALL_APPS, false);
            mUninstallDisabledCache.put(info.user, uninstallDisabled);
        }

        // Cancel any pending alarm and set cache expiry after some time
        if (uninstallDisabled) {// 不允许卸载
            return false;
        }

        if (info instanceof ItemInfoWithIcon) {
            ItemInfoWithIcon iconInfo = (ItemInfoWithIcon) info;
            if ((iconInfo.runtimeStatusFlags & FLAG_SYSTEM_MASK) != 0) {
                return (iconInfo.runtimeStatusFlags & FLAG_SYSTEM_NO) != 0;
            }
        }
        return getUninstallTarget(launcher, info) != null;
    }

    /**
     * @return the component name that should be uninstalled or null.
     */
    private static ComponentName getUninstallTarget(Launcher launcher, ItemInfo item) {
        Intent intent = null;
        UserHandle user = null;
        if (item != null &&
                item.itemType == LauncherSettings.BaseLauncherColumns.ITEM_TYPE_APPLICATION) {
            intent = item.getIntent();
            user = item.user;
        }
        if (intent != null) {
            LauncherActivityInfo info = LauncherAppsCompat.getInstance(launcher)
                    .resolveActivity(intent, user);
            if (info != null
                    && (info.getApplicationInfo().flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
                return info.getComponentName();
            }
        }
        return null;
    }

    /**
     * Verifies that the view is an reconfigurable widget and returns the corresponding widget Id,
     * otherwise return {@code INVALID_APPWIDGET_ID}
     */
    private static int getReconfigurableWidgetId(View view) {
        if (!(view instanceof AppWidgetHostView)) {
            return INVALID_APPWIDGET_ID;
        }
        AppWidgetHostView hostView = (AppWidgetHostView) view;
        AppWidgetProviderInfo widgetInfo = hostView.getAppWidgetInfo();
        if (widgetInfo == null || widgetInfo.configure == null) {
            return INVALID_APPWIDGET_ID;
        }
        if ((LauncherAppWidgetProviderInfo.fromProviderInfo(view.getContext(), widgetInfo)
                .getWidgetFeatures() & WIDGET_FEATURE_RECONFIGURABLE) == 0) {
            return INVALID_APPWIDGET_ID;
        }
        return hostView.getAppWidgetId();
    }


    /**
     * Performs the drop action and returns the target component for the dragObject or null if
     * the action was not performed.
     */
    public static ComponentName startUninstallApk(Launcher launcher, ItemInfo info) {

        ComponentName cn = getUninstallTarget(launcher, info);
        if (cn == null) {
            // System applications cannot be installed. For now, show a toast explaining that.
            // We may give them the option of disabling apps this way.
            Toast.makeText(launcher, R.string.uninstall_system_app_text, Toast.LENGTH_SHORT).show();
            return null;
        }
        try {
            Intent i = Intent.parseUri(launcher.getString(R.string.delete_package_intent), 0)
                    .setData(Uri.fromParts("package", cn.getPackageName(), cn.getClassName()))
                    .putExtra(Intent.EXTRA_USER, info.user);
            launcher.startActivity(i);
            return cn;
        } catch (URISyntaxException e) {
            XLog.e(XLog.getTag(), XLog.TAG_GU + "Failed to parse intent to start uninstall activity for item=" + info);
            return null;
        }
    }

}
