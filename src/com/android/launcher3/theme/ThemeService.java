package com.android.launcher3.theme;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v4.content.LocalBroadcastManager;

import com.android.mxlibrary.util.XLog;
import com.android.mxtheme.IRemoteCallback;
import com.android.mxtheme.IThemeService;
import com.android.mxtheme.bean.ThemeBean;
import com.android.mxtheme.bean.WallpaperBean;

import java.util.ArrayList;
import java.util.List;

/**
 * 切换主题或者壁纸的服务，通过本地广播通知Launcher更换主题或者壁纸。
 * <p>
 * Created by CodeMX
 * DATE 2019/2/22
 * TIME 10:43
 */
public class ThemeService extends Service {

    public static final String ACTION_SET_THEME = "com.android.mxlauncher.THEME";
    public static final String ACTION_SET_THEME_SUCCESS = "com.android.mxlauncher.THEME_SUCCESS";
    public static final String ACTION_SET_THEME_FAIL = "com.android.mxlauncher.THEME_FAIL";
    public static final String ACTION_SET_WALLPAPER = "com.android.mxlauncher.WALLPAPER";
    public static final String ACTION_SET_WALLPAPER_SUCCESS = "com.android.mxlauncher.WALLPAPER_SUCCESS";
    public static final String ACTION_SET_WALLPAPER_FAIL = "com.android.mxlauncher.WALLPAPER_FAIL";
    public static final int STATUS_SET_THEME_SUCCESS = 100;
    public static final int STATUS_SET_THEME_FAIL = -101;
    public static final int STATUS_SET_WALLPAPER_SUCCESS = 1000;
    public static final int STATUS_SET_WALLPAPER_FAIL = -1001;

    private ThemeBinder mThemeBinder;
    private LocalBroadcastManager mLocalBroadcastManager;
    private List<IRemoteCallback> mIRemoteCallbackList;

    // 切换主题、壁纸的广播
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (ACTION_SET_THEME_SUCCESS.equals(action)) {// 设置主题成功

            } else if (ACTION_SET_THEME_FAIL.equals(action)) {// 设置主题失败

            } else if (ACTION_SET_WALLPAPER_SUCCESS.equals(action)) {// 设置壁纸成功

            } else if (ACTION_SET_WALLPAPER_FAIL.equals(action)) {// 设置壁纸失败

            }
        }
    };

    @Override
    public IBinder onBind(Intent intent) {
        return mThemeBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mThemeBinder = new ThemeBinder();
        mIRemoteCallbackList = new ArrayList<>();
        mLocalBroadcastManager = LocalBroadcastManager.getInstance(this);
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_SET_THEME_SUCCESS);
        filter.addAction(ACTION_SET_THEME_FAIL);
        filter.addAction(ACTION_SET_WALLPAPER_SUCCESS);
        filter.addAction(ACTION_SET_WALLPAPER_FAIL);
        mLocalBroadcastManager.registerReceiver(mReceiver, filter);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mReceiver);
        super.onDestroy();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    @Override
    public void onRebind(Intent intent) {
        super.onRebind(intent);
    }

    private class ThemeBinder extends IThemeService.Stub {

        @Override
        public void register(IRemoteCallback callback) throws RemoteException {
            mIRemoteCallbackList.add(callback);
        }

        @Override
        public void unRegister(IRemoteCallback callback) throws RemoteException {
            mIRemoteCallbackList.remove(callback);
        }

        @Override
        public boolean setTheme(ThemeBean themeBean) throws RemoteException {
            if (themeBean != null) {
                XLog.e(XLog.getTag(), XLog.TAG_GU + themeBean.toString());
                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putParcelable("theme", themeBean);
                intent.putExtra("theme", bundle);
                mLocalBroadcastManager.sendBroadcastSync(intent);
            } else {
                XLog.e(XLog.getTag(), XLog.TAG_GU + "themeBean is null");
            }
            return true;
        }

        @Override
        public boolean setWallpaper(WallpaperBean wallpaperBean) throws RemoteException {
            if (wallpaperBean != null) {
                XLog.e(XLog.getTag(), XLog.TAG_GU + wallpaperBean.toString());
                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putParcelable("wallpaper", wallpaperBean);
                intent.putExtra("wallpaper", bundle);
                mLocalBroadcastManager.sendBroadcastSync(intent);
            } else {
                XLog.e(XLog.getTag(), XLog.TAG_GU + "wallpaperBean is null");
            }
            return true;
        }
    }


}
