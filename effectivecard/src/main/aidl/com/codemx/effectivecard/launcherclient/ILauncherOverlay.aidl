// ILauncherOverlay.aidl
package com.codemx.effectivecard.launcherclient;

// Declare any non-default types here with import statements

import com.codemx.effectivecard.launcherclient.MxLayoutParams;
import com.codemx.effectivecard.launcherclient.ILauncherOverlayCallback;

interface ILauncherOverlay {

    void startScroll();
    
    void onScroll(float progress, boolean isRtl) ;
    
    void endScroll() ;
    
    void windowAttached(in MxLayoutParams layoutParams, ILauncherOverlayCallback overlayCallback, int flags) ;
    
    void windowDetached(boolean isChangingConfigurations) ;
    
    void closeOverlay(int flags) ;
    
    void onPause() ;
    
    void onResume() ;
    
    void openOverlay(int flags) ;
    
    void requestVoiceDetection(boolean start) ;
    
    String getVoiceSearchLanguage() ;
    
    boolean isVoiceDetectionRunning() ;
    
    void enableScroll(boolean left, boolean right) ;
    
    void enableTransparentWallpaper(boolean isTransparent) ;
    
    void enableLoopWithOverlay(boolean enableLoop) ;
}
