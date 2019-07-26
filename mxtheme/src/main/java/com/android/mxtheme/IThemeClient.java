package com.android.mxtheme;

import android.content.Context;
import android.os.IBinder;

import com.android.mxtheme.bean.ThemeBean;
import com.android.mxtheme.bean.WallpaperBean;

public interface IThemeClient {

    void bindService(Context context);

    void unbindService(Context context);

    void changeTheme(ThemeBean themeBean);

    void changeWallpaper(WallpaperBean wallpaperBean);

    void registerRemoteCallback();

    void unRegisterRemoteCallback();

    void linkToDeath(IBinder service);

    void unlinkToDeath();

}
