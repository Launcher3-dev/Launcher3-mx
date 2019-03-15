// IRemoteCallback.aidl
package com.android.mxtheme;

import com.android.mxtheme.bean.ThemeBean;
import com.android.mxtheme.bean.WallpaperBean;

interface IRemoteCallback {

    /**
     * 设置主题成功回调
     *
     * @param bean 设置的主题对象
     */
    void onThemeSuccess(in ThemeBean bean);

    /**
     * 设置主题失败回调
     *
     * @param errMsg 错误信息
     * @param bean   设置失败的主题
     */
    void onThemeFail(in String errMsg, in ThemeBean bean);

    /**
     * 设置壁纸成功回调
     *
     * @param bean 设置的壁纸对象
     */
    void onWallpaperSuccess(in WallpaperBean bean);

    /**
     * 设置壁纸失败回调
     *
     * @param errMsg 错误信息
     * @param bean   设置失败的壁纸
     */
    void onWallpaperFail(in String errMsg, in WallpaperBean bean);

}
