package com.codemx.effectivecard.launcherclient;

import android.content.Context;
import android.os.IBinder;
import android.os.RemoteException;

public class HotwordServiceChecker extends AbsHotwordServiceChecker {

    public HotwordServiceChecker(Context context) {
        super(context);
    }

    public void checkHotwordService(StatusCallback statusCallback) {
        this.checkHotwordService(statusCallback, LauncherClient.getServiceIntent(this.mContext, Constant.GSA_PACKAGE));
    }

    protected boolean getStatus(IBinder service) throws RemoteException {
        return ILauncherOverlay.Stub.asInterface(service).isVoiceDetectionRunning();
    }
}
