package com.google.android.libraries.gsa.launcherclient;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;

import com.google.android.launcherclient.Constant;

final class UpdateReceiver extends BroadcastReceiver {

    private final LauncherClient mClient;

    UpdateReceiver(LauncherClient launcherClient) {
        this.mClient = launcherClient;
    }

    public final void onReceive(Context context, Intent intent) {
        Uri data = intent.getData();
        if (Build.VERSION.SDK_INT >= 19 || (data != null && Constant.GSA_PACKAGE.equals(data.getSchemeSpecificPart()))) {
            this.mClient.mSimpleServiceConnection.unbindService();
            this.mClient.sApplicationConnection.unbindService();
            LauncherClient.loadApiVersion(context);
            if ((this.mClient.mState & 2) != 0) {
                this.mClient.reconnect();
            }
        }
    }
}
