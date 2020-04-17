package com.google.android.libraries.gsa.launcherclient;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

final class HotwordServiceConnection implements ServiceConnection {

    private AbsServiceStatusChecker.StatusCallback statusCallback;

    private final AbsServiceStatusChecker absServiceStatusChecker;

    public HotwordServiceConnection(AbsServiceStatusChecker absServiceStatusChecker, AbsServiceStatusChecker.StatusCallback statusCallback) {
        this.absServiceStatusChecker = absServiceStatusChecker;
        this.statusCallback = statusCallback;
    }

    public final void onServiceDisconnected(ComponentName componentName) {
    }

    public final void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        try {
            this.statusCallback.isRunning(this.absServiceStatusChecker.getStatus(iBinder));
            this.absServiceStatusChecker.mContext.unbindService(this);
        } catch (RemoteException e) {
            Log.w("AbsServiceStatusChecker", "isServiceRunning - remote call failed", e);
            this.absServiceStatusChecker.mContext.unbindService(this);
            this.statusCallback.isRunning(false);
        } catch (Throwable th) {
            this.absServiceStatusChecker.mContext.unbindService(this);
            throw th;
        }
    }
}
