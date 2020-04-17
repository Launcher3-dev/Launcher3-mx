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
 */
public class OverlayCallbacks extends ILauncherOverlayCallback.Stub implements Handler.Callback {
    private static final int MSG_UPDATE_SCROLL = 2;
    private static final int MSG_UPDATE_SHIFT = 3;
    private static final int MSG_UPDATE_STATUS = 4;
    private final Handler mUIHandler = new Handler(Looper.getMainLooper(), this);
    private LauncherClient mClient;
    private WindowManager mWindowManager;
    private int mWindowShift;
    private Window mWindow;
    private boolean mWindowHidden = false;

    OverlayCallbacks() {
    }

    public void setClient(LauncherClient client) {
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
        this.mUIHandler.removeMessages(2);
        Message.obtain(this.mUIHandler, 2, progress).sendToTarget();
        if (progress > 0.0F) {
            this.hideActivityNonUI(false);
        }

    }

    public void overlayStatusChanged(int status) {
        Log.d("LauncherClient", "overlayStatusChanged status=" + status);
        Message.obtain(this.mUIHandler, 4, status, 0).sendToTarget();
    }

    @Override
    public void requestStatusbarState(int state) throws RemoteException {
        Message.obtain(this.mUIHandler, 5, state, 0).sendToTarget();
    }

    @Override
    public void requestSearchActivity() throws RemoteException {
        Message.obtain(this.mUIHandler, 6).sendToTarget();
    }

    public boolean handleMessage(Message msg) {
        if (this.mClient == null) {
            return true;
        } else {
            switch (msg.what) {
                case 2:
                    if ((this.mClient.getServiceStatus() & 1) != 0) {
                        this.mClient.getLauncherClientCallbacks().onOverlayScrollChanged((Float) msg.obj);
                    }

                    return true;
                case 3:
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
                case 4:
                    this.mClient.notifyStatusChanged(msg.arg1);
                    return true;
                case 5:
                    if ((this.mClient.getServiceStatus() & 1) != 0) {
                        this.mClient.getLauncherClientCallbacks().requestStatusbarState(msg.arg1);
                    }
                    return true;
                case 6:
                    if ((this.mClient.getServiceStatus() & 1) != 0) {
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