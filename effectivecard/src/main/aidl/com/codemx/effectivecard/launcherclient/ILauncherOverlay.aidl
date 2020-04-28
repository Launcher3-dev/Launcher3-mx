// ILauncherOverlay.aidl
package com.codemx.effectivecard.launcherclient;

// Declare any non-default types here with import statements

import com.codemx.effectivecard.launcherclient.MxLayoutParams;
import com.codemx.effectivecard.launcherclient.ILauncherOverlayCallback;

interface ILauncherOverlay {

    void startScroll();
    
    void onScroll(float progress, boolean isRtl);
    
    void endScroll();

    // layoutParams：Launcher的Window.LayoutParams；
    // overlayCallback：负一屏向Launcher回传状态的回调；
    // flags：Launcher端控制连接哪个服务，负一屏可以有多个服务，或者说多个负一屏。
    void windowAttached(in MxLayoutParams layoutParams, ILauncherOverlayCallback overlayCallback, int flags);
    
    void windowDetached(boolean isChangingConfigurations);

    // flags：0，直接打开负一屏；1，动画效果打开负一屏
    void openOverlay(int flags);

    // flags：0，直接关闭负一屏；1，动画效果关闭负一屏
    void closeOverlay(int flags);
    
    void onResume();

    void onPause();

    void requestVoiceDetection(boolean start);
    
    String getVoiceSearchLanguage();
    
    boolean isVoiceDetectionRunning();
    
    void enableScroll(boolean left, boolean right);
    
    void enableTransparentWallpaper(boolean isTransparent);
    
    void enableLoopWithOverlay(boolean enableLoop);
}
