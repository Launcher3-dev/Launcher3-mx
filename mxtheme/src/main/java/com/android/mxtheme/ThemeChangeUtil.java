package com.android.mxtheme;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;

import com.android.mxlibrary.util.XLog;
import com.android.mxtheme.bean.ThemeBean;
import com.android.mxtheme.bean.WallpaperBean;

/**
 * Created by CodeMX
 * DATE 2019/2/22
 * TIME 10:50
 */
public class ThemeChangeUtil {

    private IThemeService mIThemeService;
    private ThemeServiceDeathRecipient mThemeServiceDeathRecipient;

    private IRemoteCallback mIRemoteCallback = new IRemoteCallback.Stub() {
        @Override
        public void onThemeSuccess(ThemeBean bean) throws RemoteException {

        }

        @Override
        public void onThemeFail(String errMsg, ThemeBean bean) throws RemoteException {

        }

        @Override
        public void onWallpaperSuccess(WallpaperBean bean) throws RemoteException {

        }

        @Override
        public void onWallpaperFail(String errMsg, WallpaperBean bean) throws RemoteException {

        }
    };


    // 监听远程服务是否挂掉了
    private class ThemeServiceDeathRecipient implements IBinder.DeathRecipient {

        @Override
        public void binderDied() {
            // 远程服务挂掉处理（检测主题是否切换完成，没有切换完成需要重新处理）
        }
    }

    private ServiceConnection mThemeConnection = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {
            if (mIThemeService != null) {
                try {
                    mIThemeService.unRegister(mIRemoteCallback);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                mIThemeService = null;
            }
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mIThemeService = IThemeService.Stub.asInterface(service);
            try {
                if (mIThemeService != null) {
                    mIThemeService.register(mIRemoteCallback);
                }
                service.linkToDeath(mThemeServiceDeathRecipient, 0);
            } catch (RemoteException e) {// 要链接的服务已经挂掉
                e.printStackTrace();
            }
        }
    };

    ThemeChangeUtil() {
        this.mThemeServiceDeathRecipient = new ThemeServiceDeathRecipient();
    }

    /**
     * 开始服务
     */
    void startService(Context context) {
        Intent service = new Intent();
        service.setAction("cn.bgxt.Service.CUSTOM_TYPE_SERVICE");
        service.setClassName("com.android.launcher3",
                "com.android.launcher3.theme.ThemeService");
        context.bindService(service, mThemeConnection, Service.BIND_AUTO_CREATE);
    }

    /**
     * 停止服务
     */
    void endService(Context context) {
        context.unbindService(mThemeConnection);
    }

    void changeTheme(ThemeBean themeBean) {
        if (mIThemeService != null) {
            try {
                mIThemeService.setTheme(themeBean);
            } catch (RemoteException e) {
                XLog.e(XLog.getTag(), XLog.TAG_GU + "change theme failed!!!");
            }
        }
    }

    void changeWallpaper(WallpaperBean wallpaperBean) {
        if (mIThemeService != null) {
            try {
                mIThemeService.setWallpaper(wallpaperBean);
            } catch (RemoteException e) {
                XLog.e(XLog.getTag(), XLog.TAG_GU + "change wallpaper failed !!!");
            }
        }
    }

}
