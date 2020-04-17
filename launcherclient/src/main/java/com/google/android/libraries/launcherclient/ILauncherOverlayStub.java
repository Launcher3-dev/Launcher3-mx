package com.google.android.libraries.launcherclient;

import android.os.IBinder;
import android.os.IInterface;
import com.google.android.launcherclient.BaseStub;

public abstract class ILauncherOverlayStub extends BaseStub implements ILauncherOverlay {

    protected ILauncherOverlayStub(String str) {
        super(str);
    }

    public static ILauncherOverlay asInterface(IBinder iBinder) {
        if (iBinder == null) {
            return null;
        }
        IInterface queryLocalInterface = iBinder.queryLocalInterface("com.google.android.libraries.launcherclient.ILauncherOverlay");
        if (queryLocalInterface instanceof ILauncherOverlay) {
            return (ILauncherOverlay) queryLocalInterface;
        }
        return new LauncherOverlayProxy(iBinder);
    }
}
