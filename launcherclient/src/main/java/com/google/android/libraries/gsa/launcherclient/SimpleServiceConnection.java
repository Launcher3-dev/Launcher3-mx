package com.google.android.libraries.gsa.launcherclient;

import android.content.ComponentName;
import android.content.Context;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

class SimpleServiceConnection implements ServiceConnection {

    private final Context f44a;

    private final int f45b;

    private boolean f46c;

    SimpleServiceConnection(Context context, int i) {
        this.f44a = context;
        this.f45b = i;
    }

    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
    }

    public void onServiceDisconnected(ComponentName componentName) {
    }

    public final void unbindService() {
        if (this.f46c) {
            this.f44a.unbindService(this);
            this.f46c = false;
        }
    }

    public final boolean mo73b() {
        return this.f46c;
    }

    public final boolean mo74c() {
        if (!this.f46c) {
            try {
                this.f46c = this.f44a.bindService(LauncherClient.getIntent(this.f44a), this, this.f45b);
            } catch (SecurityException e) {
                Log.e("LauncherClient", "Unable to connect to overlay service", e);
            }
        }
        return this.f46c;
    }
}
