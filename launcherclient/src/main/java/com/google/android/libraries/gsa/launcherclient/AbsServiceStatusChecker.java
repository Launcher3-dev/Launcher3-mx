package com.google.android.libraries.gsa.launcherclient;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.RemoteException;

import com.google.android.launcherclient.Constant;

public abstract class AbsServiceStatusChecker {

    final Context mContext;

    public interface StatusCallback {
        void isRunning(boolean z);
    }

    protected AbsServiceStatusChecker(Context context) {
        this.mContext = context;
    }

    public abstract boolean getStatus(IBinder iBinder) throws RemoteException;

    public final void checkStatusService(StatusCallback statusCallback, Intent intent) {
        intent.setPackage(Constant.GSA_PACKAGE);
        if (!this.mContext.bindService(intent, new HotwordServiceConnection(this, statusCallback), Context.BIND_AUTO_CREATE)) {
            new Handler(Looper.getMainLooper()).post(new ServiceStatusRunnable(this, statusCallback));
        }
    }

    public static void assertMainThread() {
        if (Looper.getMainLooper().getThread() != Thread.currentThread()) {
            throw new IllegalStateException("Must be called on the main thread.");
        }
    }
}
