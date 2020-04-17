package com.codemx.effectivecard.launcherclient;

public interface LauncherClientCallbacks {
    void onOverlayScrollChanged(float var1);

    void onServiceStateChanged(boolean var1, boolean var2);

    void requestStatusbarState(int state);

    void requestSearchActivity();
}
