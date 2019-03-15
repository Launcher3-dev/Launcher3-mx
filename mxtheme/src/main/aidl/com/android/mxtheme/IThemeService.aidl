// IThemeService.aidl
package com.android.mxtheme;

import com.android.mxtheme.IRemoteCallback;
import com.android.mxtheme.bean.ThemeBean;
import com.android.mxtheme.bean.WallpaperBean;

interface IThemeService {

    /**
     * 注册回调
     *
     * @param callback 回调
     */
    void register(IRemoteCallback callback);

    /**
     * 取消注册回调
     *
     * @param callback 回调
     */
    void unRegister(IRemoteCallback callback);

    /**
     * 除了基本数据类型，其他类型的参数都需要标上方向类型：in(输入), out(输出), inout(输入输出)
     */
    boolean setTheme(in ThemeBean themeBean);

    /**
     * 除了基本数据类型，其他类型的参数都需要标上方向类型：in(输入), out(输出), inout(输入输出)
     */
    boolean setWallpaper(in WallpaperBean wallpaperBean);

}
