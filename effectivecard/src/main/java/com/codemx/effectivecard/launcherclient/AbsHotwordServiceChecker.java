package com.codemx.effectivecard.launcherclient;

import android.annotation.TargetApi;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.RemoteException;
import android.util.Log;

@TargetApi(19)
public abstract class AbsHotwordServiceChecker {
    private static final boolean DBG = false;
    private static final String TAG = "AbsHotwordServiceChecker";
    final Context mContext;

    protected AbsHotwordServiceChecker(Context context) {
        this.mContext = context;
    }

    protected void checkHotwordService(final StatusCallback statusCallback, Intent intent) {
        intent.setPackage(Constant.GSA_PACKAGE);
        HotwordServiceConnection connection = new HotwordServiceConnection(statusCallback);
        boolean available = this.mContext.bindService(intent, connection, 1);
        if(!available) {
            (new Handler(Looper.getMainLooper())).post(new Runnable() {
                public void run() {
                    AbsHotwordServiceChecker.this.assertMainThread();
                    statusCallback.isRunning(false);
                }
            });
        }

    }

    private void assertMainThread() {
        if(Looper.getMainLooper().getThread() != Thread.currentThread()) {
            throw new IllegalStateException("Must be called on the main thread.");
        }
    }

    protected abstract boolean getStatus(IBinder var1) throws RemoteException;

    private class HotwordServiceConnection implements ServiceConnection {
        private StatusCallback mStatusCallback;

        public HotwordServiceConnection(StatusCallback statusCallback) {
            this.mStatusCallback = statusCallback;
        }

        public void onServiceDisconnected(ComponentName cn) {
        }

        public void onServiceConnected(ComponentName cn, IBinder service) {
            try {
                this.mStatusCallback.isRunning(AbsHotwordServiceChecker.this.getStatus(service));
                return;
            } catch (RemoteException var7) {
                Log.w("AbsHotwordServiceChecker", "isHotwordServiceRunning - remote call failed", var7);
            } finally {
                AbsHotwordServiceChecker.this.mContext.unbindService(this);
            }

            this.mStatusCallback.isRunning(false);
        }
    }

    public interface StatusCallback {
        void isRunning(boolean var1);
    }
}
