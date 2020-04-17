package com.android.launcher3;

import android.content.Intent;
import android.os.Bundle;

import com.codemx.effectivecard.launcherclient.LauncherClient;

import java.io.FileDescriptor;
import java.io.PrintWriter;

/**
 * Created by yuchuan
 * DATE 2020/4/17
 * TIME 16:46
 */
public class LauncherCallbacksImpl implements LauncherCallbacks {

    private LauncherClient mClient;

    public LauncherCallbacksImpl(LauncherClient client) {
        mClient = client;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
    }

    @Override
    public void onResume() {
        mClient.onResume();
    }

    @Override
    public void onStart() {

    }

    @Override
    public void onStop() {

    }

    @Override
    public void onPause() {
        mClient.onPause();
    }

    @Override
    public void onDestroy() {
        mClient.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

    }

    @Override
    public void onAttachedToWindow() {
        mClient.onAttachedToWindow();
    }

    @Override
    public void onDetachedFromWindow() {
        mClient.onDetachedFromWindow();
    }

    @Override
    public void dump(String prefix, FileDescriptor fd, PrintWriter w, String[] args) {

    }

    @Override
    public void onHomeIntent(boolean internalStateHandled) {

    }

    @Override
    public boolean handleBackPressed() {
        return false;
    }

    @Override
    public void onTrimMemory(int level) {

    }

    @Override
    public void onLauncherProviderChange() {

    }

    @Override
    public boolean startSearch(String initialQuery, boolean selectInitialQuery, Bundle appSearchData) {
        return false;
    }

    @Override
    public boolean hasSettings() {
        return false;
    }

    @Override
    public boolean hasCustomContentToLeft() {// 放到Workspace中的负一屏，如果Window负一屏开启，这个关闭
        return mClient == null;
    }

    @Override
    public void populateCustomContentContainer() {

    }
}
