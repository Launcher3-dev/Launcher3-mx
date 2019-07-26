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
public class ThemeClient implements IThemeClient {

    private IThemeService mIThemeService;
    private ThemeServiceDeathRecipient mThemeServiceDeathRecipient;

    // 返回一个Binder对象
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
        public void onServiceConnected(ComponentName name, IBinder service) {
            mIThemeService = IThemeService.Stub.asInterface(service);
            linkToDeath(service);
            registerRemoteCallback();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            XLog.e(XLog.getTag(), XLog.TAG_GU + "onServiceDisconnected:  " + name);
        }
    };

    ThemeClient() {
        this.mThemeServiceDeathRecipient = new ThemeServiceDeathRecipient();
    }

    /**
     * 绑定服务
     */
    @Override
    public void bindService(Context context) {
        Intent service = new Intent();
        service.setAction("action.mxlauncher3.ThemeService");
        context.bindService(service, mThemeConnection, Service.BIND_AUTO_CREATE);
    }

    /**
     * 解除绑定服务
     */
    @Override
    public void unbindService(Context context) {
        unRegisterRemoteCallback();
        unlinkToDeath();
        context.unbindService(mThemeConnection);
        mIThemeService = null;
    }

    /**
     * 更改主题
     *
     * @param themeBean 主题对象
     */
    @Override
    public void changeTheme(ThemeBean themeBean) {
        if (mIThemeService != null) {
            try {
                mIThemeService.setTheme(themeBean);
            } catch (RemoteException e) {
                XLog.e(XLog.getTag(), XLog.TAG_GU + "change theme failed!!!");
            }
        }
    }

    /**
     * 更改壁纸
     *
     * @param wallpaperBean 壁纸对象
     */
    @Override
    public void changeWallpaper(WallpaperBean wallpaperBean) {
        if (mIThemeService != null) {
            try {
                mIThemeService.setWallpaper(wallpaperBean);
            } catch (RemoteException e) {
                XLog.e(XLog.getTag(), XLog.TAG_GU + "change wallpaper failed !!!");
            }
        }
    }

    @Override
    public void registerRemoteCallback() {
        if (mIThemeService != null) {
            try {
                mIThemeService.register(mIRemoteCallback);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void unRegisterRemoteCallback() {
        if (mIThemeService != null) {
            try {
                mIThemeService.unRegister(mIRemoteCallback);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void linkToDeath(IBinder service) {
        if (service != null) {
            try {
                service.linkToDeath(mThemeServiceDeathRecipient, 0);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void unlinkToDeath() {
        if (mIThemeService != null) {
            mIThemeService.asBinder().unlinkToDeath(mThemeServiceDeathRecipient, 0);
        }
    }

}
