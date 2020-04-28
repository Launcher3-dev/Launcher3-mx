package com.codemx.effectivecard.launcherclient;

import android.graphics.Point;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

/**
 * Created by yuchuan
 * DATE 2020/4/17
 * TIME 15:12
 * <p>
 * 负一屏向Launcher通信的回调。
 */
public class LauncherOverlayCallbacks extends ILauncherOverlayCallback.Stub implements Handler.Callback {
    private static final int MSG_UPDATE_SCROLL = 2;
    private static final int MSG_UPDATE_SHIFT = 3;
    private static final int MSG_UPDATE_STATUS = 4;
    private static final int MSG_UPDATE_STATUSBAR = 5;
    private static final int MSG_UPDATE_REQUEST_ACTIVITY = 6;
    private final Handler mUIHandler = new Handler(Looper.getMainLooper(), this);
    private LauncherClient mClient;
    private WindowManager mWindowManager;
    private int mWindowShift;
    private Window mWindow;
    private boolean mWindowHidden = false;

    LauncherOverlayCallbacks() {
    }

    void setClient(LauncherClient client) {
        this.mClient = client;
        this.mWindowManager = client.getActivity().getWindowManager();
        Point p = new Point();
        this.mWindowManager.getDefaultDisplay().getRealSize(p);
        this.mWindowShift = -Math.max(p.x, p.y);
        this.mWindow = client.getActivity().getWindow();
    }

    public void clear() {
        this.mClient = null;
        this.mWindowManager = null;
        this.mWindow = null;
    }

    public void overlayScrollChanged(float progress) throws RemoteException {
        this.mUIHandler.removeMessages(MSG_UPDATE_SCROLL);
        Message.obtain(this.mUIHandler, MSG_UPDATE_SCROLL, progress).sendToTarget();
        if (progress > 0.0F) {
            this.hideActivityNonUI(false);
        }

    }

    public void overlayStatusChanged(int status) {
        Log.d("LauncherClient", "overlayStatusChanged status=" + status);
        Message.obtain(this.mUIHandler, MSG_UPDATE_STATUS, status, 0).sendToTarget();
    }

    @Override
    public void requestStatusBarState(int state) throws RemoteException {
        Message.obtain(this.mUIHandler, MSG_UPDATE_STATUSBAR, state, 0).sendToTarget();
    }

    @Override
    public void requestSearchActivity() throws RemoteException {
        Message.obtain(this.mUIHandler, MSG_UPDATE_REQUEST_ACTIVITY).sendToTarget();
    }

    public boolean handleMessage(Message msg) {
        if (this.mClient == null) {
            return true;
        } else {
            switch (msg.what) {
                case MSG_UPDATE_SCROLL:
                    if ((this.mClient.getServiceStatus() & LauncherClient.STATE_CONNECTED) != 0) {
                        this.mClient.getLauncherClientCallbacks().onOverlayScrollChanged((Float) msg.obj);
                    }
                    return true;
                case MSG_UPDATE_SHIFT:
                    WindowManager.LayoutParams attrs = this.mWindow.getAttributes();
                    if ((Boolean) msg.obj) {
                        attrs.x = this.mWindowShift;
                        attrs.flags |= 512;
                    } else {
                        attrs.x = 0;
                        attrs.flags &= -513;
                    }
                    this.mWindowManager.updateViewLayout(this.mWindow.getDecorView(), attrs);
                    return true;
                case MSG_UPDATE_STATUS:
                    this.mClient.notifyStatusChanged(msg.arg1);
                    return true;
                case MSG_UPDATE_STATUSBAR:
                    if ((this.mClient.getServiceStatus() & LauncherClient.STATE_CONNECTED) != 0) {
                        this.mClient.getLauncherClientCallbacks().requestStatusBarState(msg.arg1);
                    }
                    return true;
                case MSG_UPDATE_REQUEST_ACTIVITY:
                    if ((this.mClient.getServiceStatus() & LauncherClient.STATE_CONNECTED) != 0) {
                        this.mClient.getLauncherClientCallbacks().requestSearchActivity();
                    }
                    return true;
                default:
                    return false;
            }
        }
    }

    private void hideActivityNonUI(boolean isHidden) {
        if (this.mWindowHidden != isHidden) {
            this.mWindowHidden = isHidden;
        }

    }
}