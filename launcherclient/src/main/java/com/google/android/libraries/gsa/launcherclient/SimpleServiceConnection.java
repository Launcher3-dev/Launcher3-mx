package com.google.android.libraries.gsa.launcherclient;

import android.content.ComponentName;
import android.content.Context;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

class SimpleServiceConnection implements ServiceConnection {

    private final Context mContext;

    private final int flags;

    private boolean isConnected;

    SimpleServiceConnection(Context context, int flags) {
        this.mContext = context;
        this.flags = flags;
    }

    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
    }

    public void onServiceDisconnected(ComponentName componentName) {
    }

    public final void unbindService() {
        if (this.isConnected) {
            this.mContext.unbindService(this);
            this.isConnected = false;
        }
    }

    public final boolean isConnected() {
        return this.isConnected;
    }

    public final boolean reconnect() {
        if (!this.isConnected) {
            try {
                this.isConnected = this.mContext.bindService(LauncherClient.getIntent(this.mContext), this, this.flags);
            } catch (SecurityException e) {
                Log.e("LauncherClient", "Unable to connect to overlay service", e);
            }
        }
        return this.isConnected;
    }
}
