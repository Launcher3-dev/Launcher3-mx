package com.android.launcher3.theme;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v4.content.LocalBroadcastManager;

import com.android.mxlibrary.util.XLog;
import com.android.mxtheme.bean.IThemeInterface;
import com.android.mxtheme.bean.ThemeBean;
import com.android.mxtheme.bean.WallpaperBean;

/**
 * 切换主题或者壁纸的服务，通过本地广播通知Launcher更换主题或者壁纸。
 * <p>
 * Created by CodeMX
 * DATE 2019/2/22
 * TIME 10:43
 */
public class ThemeService extends Service {

    public static final String ACTION_THEME = "com.android.mxlauncher.THEME";
    public static final String ACTION_WALLPAPER = "com.android.mxlauncher.WALLPAPER";

    private ThemeBinder mThemeBinder;
    private LocalBroadcastManager mLocalBroadcastManager;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mThemeBinder = new ThemeBinder();
        mLocalBroadcastManager = LocalBroadcastManager.getInstance(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
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

    private class ThemeBinder extends IThemeInterface.Stub {

        @Override
        public boolean setTheme(ThemeBean themeBean) throws RemoteException {
            if (themeBean != null) {
                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putParcelable("theme", themeBean);
                intent.putExtra("theme", bundle);
                mLocalBroadcastManager.sendBroadcastSync(intent);
            }
            return true;
        }

        @Override
        public boolean setWallpaper(WallpaperBean wallpaperBean) throws RemoteException {
            XLog.e(XLog.getTag(), XLog.TAG_GU + wallpaperBean.toString());
            return false;
        }
    }

}
