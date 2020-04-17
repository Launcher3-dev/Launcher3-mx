package com.google.android.libraries.launcherclient;

import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
import android.view.WindowManager;

import com.google.android.launcherclient.BaseProxy;
import com.google.android.launcherclient.Codecs;

public final class LauncherOverlayProxy extends BaseProxy implements ILauncherOverlay {
    LauncherOverlayProxy(IBinder iBinder) {
        super(iBinder, "com.google.android.libraries.launcherclient.ILauncherOverlay");
    }

    public final void startScroll() throws RemoteException {
        transact(1, obtain());
    }

    public final void onScroll(float f) throws RemoteException {
        Parcel a = obtain();
        a.writeFloat(f);
        transact(2, a);
    }

    public final void endScroll() throws RemoteException {
        transact(3, obtain());
    }

    public final void windowAttached(WindowManager.LayoutParams layoutParams, ILauncherOverlayCallback dVar, int i) throws RemoteException {
        Parcel a = obtain();
        Codecs.writeParcelable(a, layoutParams);
        Codecs.writeInterfaceToken(a, dVar);
        a.writeInt(i);
        transact(4, a);
    }

    public final void windowAttached2(Bundle bundle, ILauncherOverlayCallback dVar) throws RemoteException {
        Parcel a = obtain();
        Codecs.writeParcelable(a, bundle);
        Codecs.writeInterfaceToken(a, dVar);
        transact(14, a);
    }

    public final void windowDetached(boolean changingConfigurations) throws RemoteException {
        Parcel a = obtain();
        Codecs.writeChangingConfiguration(a, changingConfigurations);
        transact(5, a);
    }

    public final void closeOverlay(int i) throws RemoteException {
        Parcel a = obtain();
        a.writeInt(i);
        transact(6, a);
    }

    public final void mo17c() throws RemoteException {
        transact(7, obtain());
    }

    public final void mo19d() throws RemoteException {
        transact(8, obtain());
    }

    public final void mo15b(int i) throws RemoteException {
        Parcel a = obtain();
        a.writeInt(i);
        transact(16, a);
    }

    public final void openOverlay(int i) throws RemoteException {
        Parcel a = obtain();
        a.writeInt(i);
        transact(9, a);
    }

    public final void requestVoiceDetection(boolean start) throws RemoteException {
        Parcel a = obtain();
        Codecs.writeChangingConfiguration(a, start);
        transact(10, a);
    }

    public final boolean isVoiceDetectionRunning() throws RemoteException {
        Parcel a = obtain(12, obtain());
        boolean a2 = Codecs.check(a);
        a.recycle();
        return a2;
    }

    public final boolean mo21f() throws RemoteException {
        Parcel a = obtain(13, obtain());
        boolean a2 = Codecs.check(a);
        a.recycle();
        return a2;
    }
}
