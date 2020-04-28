package com.codemx.effectivecard.launcherclient;

/**
 * Created by yuchuan
 * DATE 2020/4/28
 * TIME 16:17
 */
public interface LauncherClientCallback {

    void onOverlayScrollChanged(float progress);

    void onServiceStateChanged(boolean connected, boolean connecting);

    void requestStatusBarState(int state);

    void requestSearchActivity();

}
