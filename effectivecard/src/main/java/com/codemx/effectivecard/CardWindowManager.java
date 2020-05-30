package com.codemx.effectivecard;

import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import com.android.mxlibrary.util.XLog;
import com.codemx.effectivecard.launcherclient.ILauncherOverlayCallback;
import com.codemx.effectivecard.launcherclient.MxLayoutParams;
import com.codemx.effectivecard.launcherclient.MxMessage;
import com.codemx.floatwindow.FloatWindow;

/**
 * Created by yuchuan
 * DATE 2020/4/17
 * TIME 16:03
 */
public class CardWindowManager implements IWindowCallback {

    private Context mContext;
    private View mContentView;
    private Handler mHandler;
    private WindowManager mWindowManager;
    private WindowManager.LayoutParams mWindowParams;
    private final int mScreenWidth;
    private final int mScreenHeight;

    CardWindowManager(Context context) {
        mContext = context;
        mHandler = new Handler(Looper.getMainLooper());
        mScreenWidth = context.getResources().getDisplayMetrics().widthPixels;
        mScreenHeight = context.getResources().getDisplayMetrics().heightPixels;
    }

    private boolean isShow = false;

    private void showWindow(MxLayoutParams layoutParams) {
        if (isShow) {
            return;
        }
        isShow = true;
        mContentView = LayoutInflater.from(mContext).inflate(R.layout.layout_card, null);
        ImageView imageView = new ImageView(mContext);
        imageView.setImageResource(R.drawable.ic_home_white_24dp);

        mWindowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        mWindowParams = new WindowManager.LayoutParams();
        mWindowParams.width = layoutParams.width;
        mWindowParams.height = layoutParams.height;
        XLog.d(XLog.getTag(), "width= " + layoutParams.width + "  ,height= " + layoutParams.height);
        mWindowParams.x = 0;
        mWindowParams.y = 0;
        // 设置透明
        mWindowParams.format = PixelFormat.RGBA_8888;
        mWindowParams.gravity = Gravity.TOP | Gravity.START;
        mWindowParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                | WindowManager.LayoutParams.FLAG_FULLSCREEN
                | WindowManager.LayoutParams.FLAG_LAYOUT_INSET_DECOR
                | WindowManager.LayoutParams.FLAG_SHOW_WALLPAPER
                | WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS// 可以置顶并被状态栏覆盖
                | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS;
        mWindowParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        mWindowManager.addView(mContentView, mWindowParams);
    }

    public void hideWindow() {
        FloatWindow.destroy();
    }

    // 向Launcher通信的回调函数，对应LauncherOverlayCallbacks
    private ILauncherOverlayCallback mOverlayCallback;

    @Override
    public void startScroll() {

    }

    @Override
    public void onScroll(float progress, boolean isRtl) {
        if (mWindowManager != null) {
            mWindowParams.x = -(int) (mScreenWidth * (1 - progress));
        }
    }

    @Override
    public void endScroll() {

    }

    @Override
    public void windowAttached(final MxLayoutParams layoutParams, ILauncherOverlayCallback overlayCallback, int flags) {
        mOverlayCallback = overlayCallback;
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                try {
                    showWindow(layoutParams);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void windowDetached(boolean isChangingConfigurations) {
        hideWindow();
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
