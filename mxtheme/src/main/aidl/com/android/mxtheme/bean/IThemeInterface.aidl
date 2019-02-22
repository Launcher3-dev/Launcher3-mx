// IThemeInterface.aidl
package com.android.mxtheme.bean;

import com.android.mxtheme.bean.ThemeBean;
import com.android.mxtheme.bean.WallpaperBean;

interface IThemeInterface {

    /**
     * 除了基本数据类型，其他类型的参数都需要标上方向类型：in(输入), out(输出), inout(输入输出)
     */
    boolean setTheme(in ThemeBean themeBean);

    /**
     * 除了基本数据类型，其他类型的参数都需要标上方向类型：in(输入), out(输出), inout(输入输出)
     */
    boolean setWallpaper(in WallpaperBean wallpaperBean);

}
