package com.android.launcher3;

import android.os.Bundle;

public class LauncherPlus extends Launcher implements LauncherCallbacks{

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

    }

    @Override
    public void onHomeIntent(boolean internalStateHandled) {

    }

    @Override
    public boolean handleBackPressed() {
        return false;
    }

    @Override
    public void onLauncherProviderChange() {

    }

    @Override
    public boolean startSearch(String initialQuery, boolean selectInitialQuery, Bundle appSearchData) {
        return false;
    }

    @Override
    public boolean hasCustomContentToLeft() {
        return true;
    }

    @Override
    public void populateCustomContentContainer() {

    }


}
