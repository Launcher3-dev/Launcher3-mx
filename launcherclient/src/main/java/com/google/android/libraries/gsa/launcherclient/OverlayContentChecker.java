package com.google.android.libraries.gsa.launcherclient;

import android.content.Context;
import android.os.IBinder;
import android.os.RemoteException;

import com.google.android.libraries.launcherclient.ILauncherOverlayStub;

public class OverlayContentChecker extends AbsServiceStatusChecker {
    public OverlayContentChecker(Context context) {
        super(context);
    }

    public void checkOverlayContent(AbsServiceStatusChecker.StatusCallback statusCallback) {
        checkStatusService(statusCallback, LauncherClient.getIntent(this.mContext));
    }

    public final boolean getStatus(IBinder iBinder) throws RemoteException {
        return ILauncherOverlayStub.asInterface(iBinder).mo21f();
    }
}
