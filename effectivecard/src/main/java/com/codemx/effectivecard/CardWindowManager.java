package com.codemx.effectivecard;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.PixelFormat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;

import com.codemx.effectivecard.launcherclient.Constant;
import com.codemx.effectivecard.launcherclient.ILauncherOverlayCallback;
import com.codemx.effectivecard.launcherclient.MxLayoutParams;
import com.codemx.effectivecard.launcherclient.MxMessage;

/**
 * Created by yuchuan
 * DATE 2020/4/17
 * TIME 16:03
 */
public class CardWindowManager implements IWindowCallback{

    private Context mContext;
    private Context mCardContext;
    private WindowManager mWindowManager;
    private WindowManager.LayoutParams mParams;
    private View mContentView;

    public CardWindowManager(Context context) {
        mContext = context;
        initApplicationWindow();
    }

    private void initApplicationWindow() {
        try {
            mCardContext = mContext.createPackageContext(Constant.GSA_PACKAGE, Context.CONTEXT_IGNORE_SECURITY);
            mWindowManager = (WindowManager) mCardContext.getSystemService(Context.WINDOW_SERVICE);
            mParams = new WindowManager.LayoutParams();
            mParams.packageName = Constant.GSA_PACKAGE;
            mParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_MEDIA;
            mParams.format = PixelFormat.RGBA_8888;
            mParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS;
            mParams.gravity = Gravity.TOP | Gravity.LEFT;
            mParams.width = WindowManager.LayoutParams.MATCH_PARENT;
            mParams.height = WindowManager.LayoutParams.MATCH_PARENT;
        } catch (PackageManager.NameNotFoundException n) {
            n.printStackTrace();
        }
    }

    private void initView() {
        mContentView = LayoutInflater.from(mCardContext).inflate(R.layout.layout_card, null);
    }

    public void showWindow() {
        initView();
        mWindowManager.addView(mContentView, mParams);
    }

    public void hideWindow() {
        mWindowManager.removeView(mContentView);
    }

    // 向Launcher通信的回调函数，对应LauncherOverlayCallbacks
    private ILauncherOverlayCallback mOverlayCallback;

    @Override
    public void startScroll() {

    }

    @Override
    public void onScroll(float progress, boolean isRtl) {

    }

    @Override
    public void endScroll() {

    }

    @Override
    public void windowAttached(MxLayoutParams layoutParams, ILauncherOverlayCallback overlayCallback, int flags) {

        mOverlayCallback = overlayCallback;
    }

    @Override
    public void windowDetached(boolean isChangingConfigurations) {

    }

    @Override
    public void openOverlay(int flags) {

    }

    @Override
    public void closeOverlay(int flags) {

    }

    @Override
    public void onResume() {

    }

    @Override
    public void onPause() {

    }

    @Override
    public void onTransact(MxMessage msg) {

    }

    @Override
    public void requestVoiceDetection(boolean start) {

    }

    @Override
    public String getVoiceSearchLanguage() {
        return null;
    }

    @Override
    public boolean isVoiceDetectionRunning() {
        return false;
    }

    @Override
    public void enableScroll(boolean left, boolean right) {

    }

    @Override
    public void enableTransparentWallpaper(boolean isTransparent) {

    }

    @Override
    public void enableLoopWithOverlay(boolean enableLoop) {

    }
}
