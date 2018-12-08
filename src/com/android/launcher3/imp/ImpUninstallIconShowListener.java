package com.android.launcher3.imp;

import com.android.launcher3.uninstall.UninstallIconAnimUtil;

/**
 * 卸载监听接口
 */
public interface ImpUninstallIconShowListener {

    /**
     * 动画效果下卸载按钮的显示过度
     *
     * @param percent 百分比
     */
    void onUninstallIconChange(float percent);

    /**
     * 显示卸载按钮
     *
     * @param uninstallIconAnimUtil 动画工具
     * @param isPerformAnim         是否显示动画效果
     */
    void showUninstallIcon(UninstallIconAnimUtil uninstallIconAnimUtil, boolean isPerformAnim);

}
