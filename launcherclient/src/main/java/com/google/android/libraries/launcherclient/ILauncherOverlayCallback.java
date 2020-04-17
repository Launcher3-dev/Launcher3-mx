package com.google.android.libraries.launcherclient;

import android.os.IInterface;
import android.os.RemoteException;

public interface ILauncherOverlayCallback extends IInterface {
    void overlayScrollChanged(float f) throws RemoteException;

    void overlayStatusChanged(int i) throws RemoteException;
}
