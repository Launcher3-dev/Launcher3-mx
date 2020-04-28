package com.android.launcher3;

import com.codemx.effectivecard.launcherclient.LauncherClient;

/**
 * Created by yuchuan
 * DATE 2020/4/17
 * TIME 16:52
 */
public class LauncherOverlayImpl implements Launcher.LauncherOverlay {

    private LauncherClient mClient;

    LauncherOverlayImpl(LauncherClient client) {
        mClient = client;
    }

    @Override
    public void onScrollInteractionBegin() {
        mClient.startMove();
    }

    @Override
    public void onScrollInteractionEnd() {
        mClient.endMove();
    }

    @Override
    public void onScrollChange(float progress, boolean rtl) {
        mClient.updateMove(progress, rtl);
    }

    @Override
    public void setOverlayCallbacks(Launcher.LauncherOverlayCallbacks callbacks) {

    }
}
