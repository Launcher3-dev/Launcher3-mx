package com.google.android.libraries.gsa.launcherclient;

import android.content.ComponentName;
import android.content.Context;
import android.os.IBinder;

import com.google.android.libraries.launcherclient.ILauncherOverlayStub;
import com.google.android.libraries.launcherclient.ILauncherOverlay;

import java.lang.ref.WeakReference;

final class AppServiceConnection extends SimpleServiceConnection {

    private static AppServiceConnection f30a;

    private ILauncherOverlay f31b;

    private WeakReference<LauncherClient> f32c;

    private boolean f33d;

    static AppServiceConnection m69a(Context context) {
        if (f30a == null) {
            f30a = new AppServiceConnection(context.getApplicationContext());
        }
        return f30a;
    }

    private AppServiceConnection(Context context) {
        super(context, 33);
    }

    public final ILauncherOverlay mo58a(LauncherClient launcherClient) {
        this.f32c = new WeakReference<>(launcherClient);
        return this.f31b;
    }

    public final void mo60a(boolean z) {
        this.f33d = z;
        m71d();
    }

    public final void mo59a(LauncherClient launcherClient, boolean z) {
        LauncherClient e = m72e();
        if (e != null && e.equals(launcherClient)) {
            this.f32c = null;
            if (z) {
                unbindService();
                if (f30a == this) {
                    f30a = null;
                }
            }
        }
    }

    public final void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        m70a(ILauncherOverlayStub.asInterface(iBinder));
    }

    public final void onServiceDisconnected(ComponentName componentName) {
        m70a(null);
        m71d();
    }

    private final void m71d() {
        if (this.f33d && this.f31b == null) {
            unbindService();
        }
    }

    private final void m70a(ILauncherOverlay aVar) {
        this.f31b = aVar;
        LauncherClient e = m72e();
        if (e != null) {
            e.mo28a(this.f31b);
        }
    }

    private final LauncherClient m72e() {
        if (this.f32c != null) {
            return this.f32c.get();
        }
        return null;
    }
}
