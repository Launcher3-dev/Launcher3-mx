package com.google.android.libraries.launcherclient;

import android.os.Parcel;
import android.os.RemoteException;
import com.google.android.launcherclient.BaseStub;

public abstract class ILauncherOverlayCallbackSub extends BaseStub implements ILauncherOverlayCallback {

    public ILauncherOverlayCallbackSub() {
        super("com.google.android.libraries.launcherclient.ILauncherOverlayCallback");
    }

    public final boolean mo5a(int i, Parcel parcel) throws RemoteException {
        switch (i) {
            case 1:
                overlayScrollChanged(parcel.readFloat());
                break;
            case 2:
                overlayStatusChanged(parcel.readInt());
                break;
            default:
                return false;
        }
        return true;
    }
}
