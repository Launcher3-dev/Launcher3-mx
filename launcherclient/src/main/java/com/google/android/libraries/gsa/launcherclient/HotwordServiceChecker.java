package com.google.android.libraries.gsa.launcherclient;

import android.content.Context;
import android.os.IBinder;
import android.os.RemoteException;

import com.google.android.libraries.launcherclient.ILauncherOverlayStub;

public class HotwordServiceChecker extends AbsServiceStatusChecker {
    public HotwordServiceChecker(Context context) {
        super(context);
    }

    public void checkHotwordService(AbsServiceStatusChecker.StatusCallback statusCallback) {
        checkStatusService(statusCallback, LauncherClient.getIntent(this.mContext));
    }

    public final boolean getStatus(IBinder iBinder) throws RemoteException {
        return ILauncherOverlayStub.asInterface(iBinder).isVoiceDetectionRunning();
    }
}
