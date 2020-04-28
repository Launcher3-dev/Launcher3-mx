// ILauncherOverlayCallback.aidl
package com.codemx.effectivecard.launcherclient;

// Declare any non-default types here with import statements
// 负一屏返回给Launcher的回调函数

interface ILauncherOverlayCallback {

    // 负一屏滑动时通知桌面
    void overlayScrollChanged(float progress);

    // 负一屏状态回调
    void overlayStatusChanged(int overlayAttached);

    // 状态栏回调，需要Launcher去处理
    void requestStatusBarState(int state);

    // 请求调用搜索栏
    void requestSearchActivity();

}
