package com.android.mxtheme;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;

import com.android.mxlibrary.util.XLog;
import com.android.mxtheme.bean.IThemeInterface;
import com.android.mxtheme.bean.ThemeBean;
import com.android.mxtheme.bean.WallpaperBean;

/**
 * Created by CodeMX
 * DATE 2019/2/22
 * TIME 10:50
 */
public class ThemeChangeUtil {

    private IThemeInterface mIThemeInterface;

    private ServiceConnection mThemeConnection = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mIThemeInterface = null;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mIThemeInterface = IThemeInterface.Stub.asInterface(service);
        }
    };

    /**
     * 开始服务
     */
    private void startService(Context context) {
        Intent service = new Intent();
        service.setAction("cn.bgxt.Service.CUSTOM_TYPE_SERVICE");
        service.setClassName("com.android.launcher3", "com.android.launcher3.theme.ThemeService");
        context.bindService(service, mThemeConnection, Service.BIND_AUTO_CREATE);
    }

    /**
     * 停止服务
     */
    private void endService(Context context) {
        context.unbindService(mThemeConnection);
    }

    public void changeTheme(ThemeBean themeBean) {
        if (mIThemeInterface != null) {
            try {
                mIThemeInterface.setTheme(themeBean);
            } catch (RemoteException e) {
                XLog.e(XLog.getTag(), XLog.TAG_GU + "change theme failed!!!");
            }
        }
    }

    public void changeWallpaper(WallpaperBean wallpaperBean) {
        if (mIThemeInterface != null) {
            try {
                mIThemeInterface.setWallpaper(wallpaperBean);
            } catch (RemoteException e) {
                XLog.e(XLog.getTag(), XLog.TAG_GU + "change wallpaper failed !!!");
            }
        }
    }

}
